#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import pandas as pd
import requests
import mysql.connector
from pycpfcnpj import cpf
import re
import uuid
import unicodedata

# ==============================================================================
# CONFIGURAÇÕES GERAIS
# ==============================================================================
APP_URL = "https://realiza-api-development.onrender.com".rstrip("/")
EXCEL_FILE = "SISTEMA NOVO_ITAMINAS.xlsx"
SHEET_RESULTS = "Resultado da consulta"
CLIENT_ID = "d2bd8165-95ac-4d11-9e97-968979b9bc5f"
USER_LOGIN = {"email": "realiza@assessoria.com", "password": "senha123"}
DB_CONFIG = {
    "host": "177.170.30.9", "port": 8004, "user": "veridion_user",
    "password": "SenhaSegura123!", "database": "dbrealiza"
}
REQ_TIMEOUT = 20


# ==============================================================================
# HELPERS
# ==============================================================================
def login() -> str:
    url = f"{APP_URL}/login"
    print("Realizando login...")
    r = requests.post(url, json=USER_LOGIN, timeout=REQ_TIMEOUT)
    r.raise_for_status()
    data = r.json()
    token = data.get("token") or data.get("access_token")
    if not token: raise RuntimeError(f"Login OK, mas não veio token. Resposta: {data}")
    print("✔ Login realizado com sucesso!")
    return token


def print_summary(title: str, ok: int, already: int, fail: int, skipped: int):
    print(f"\n{title} → Criados: {ok} | Já existentes: {already} | Falhas: {fail} | Ignorados: {skipped}")


def import_data_same_folder(file_name: str, sheet_name: str) -> pd.DataFrame:
    path = os.path.join(os.path.dirname(os.path.realpath(__file__)), file_name)
    df = pd.read_excel(path, sheet_name=sheet_name, engine="openpyxl", dtype=str, keep_default_na=False)
    print(f"✔ Arquivo '{file_name}' (aba: '{sheet_name}') lido com sucesso!")
    return df


def find_column(df: pd.DataFrame, candidates, required=True):
    cols = {c.lower().strip(): c for c in df.columns}
    for cand in candidates:
        if cand.lower() in cols: return cols[cand.lower()]
    if required:
        raise ValueError(f"Coluna obrigatória não encontrada. Procure por um destes nomes: {candidates}")
    return None


def split_fullname(fullname):
    parts = str(fullname).split()
    first_name = parts[0] if parts else ""
    surname = " ".join(parts[1:]) if len(parts) > 1 else ""
    return first_name, surname


def to_date_sql(value: str) -> str:
    if not value or value == "################": return None
    try:
        d = pd.to_datetime(value, dayfirst=True, errors="coerce")
        return None if pd.isna(d) else d.strftime("%Y-%m-%d")
    except Exception:
        return None


def remove_accents(text: str) -> str:
    if not text: return ""
    return "".join(c for c in unicodedata.normalize('NFD', text) if unicodedata.category(c) != 'Mn')


def normalize_text_enum(text: str) -> str:
    if not text: return None
    text_sem_acento = remove_accents(str(text))
    return re.sub(r'[\s-]+', '_', text_sem_acento.strip()).upper()


# ==============================================================================
# BUSCA DE DADOS NO BANCO
# ==============================================================================
def fetch_initial_data_from_db(conn, cursor, client_id: str) -> (dict, dict, dict, dict, dict, str):
    print("\nBuscando mapeamentos essenciais no banco de dados...")

    cursor.execute("SELECT title, id FROM position")
    position_map = {remove_accents(str(row[0])).lower(): row[1] for row in cursor.fetchall()}
    print(f"✔ {len(position_map)} Positions carregados.")

    default_position_id = position_map.get('n/a')
    if not default_position_id:
        print("INFO: Position padrão 'N/A' não encontrada. Criando no banco...")
        new_na_id = str(uuid.uuid4())
        cursor.execute("INSERT INTO position (id, title) VALUES (%s, %s)", (new_na_id, 'N/A'))
        conn.commit()
        default_position_id = new_na_id
        position_map['n/a'] = new_na_id
        print(f"✔ Position 'N/A' criada com sucesso.")

    cursor.execute("SELECT title, id FROM cbo")
    cbo_map = {remove_accents(str(row[0])).lower(): row[1] for row in cursor.fetchall()}
    print(f"✔ {len(cbo_map)} CBOs carregados.")

    cursor.execute("SELECT name, id_branch FROM branch WHERE id_client = %s", (client_id,))
    branch_map = {remove_accents(str(row[0])).lower(): row[1] for row in cursor.fetchall()}
    print(f"✔ {len(branch_map)} Unidades encontradas para o cliente.")

    supplier_query = """
        SELECT p.corporate_name, p.id_provider 
        FROM provider p 
        JOIN provider_supplier ps ON p.id_provider = ps.id_provider
    """
    cursor.execute(supplier_query)
    supplier_map = {remove_accents(str(row[0])).lower(): row[1] for row in cursor.fetchall()}
    print(f"✔ {len(supplier_map)} Fornecedores (contratantes) carregados (global).")

    subcontractor_query = """
        SELECT p.corporate_name, p.id_provider 
        FROM provider p 
        JOIN provider_subcontractor psc ON p.id_provider = psc.id_provider
    """
    cursor.execute(subcontractor_query)
    subcontractor_map = {remove_accents(str(row[0])).lower(): row[1] for row in cursor.fetchall()}
    print(f"✔ {len(subcontractor_map)} Subcontratados carregados (global).")

    return position_map, branch_map, supplier_map, subcontractor_map, cbo_map, default_position_id


# ==============================================================================
# LÓGICA PRINCIPAL - CRIAÇÃO DE FUNCIONÁRIOS
# ==============================================================================
def create_employees(token: str, client_id: str):
    url = f"{APP_URL}/employee/brazilian"
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

    conn = None
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()

        position_map, branch_map, supplier_map, subcontractor_map, cbo_map, default_position_id = fetch_initial_data_from_db(
            conn, cursor, client_id)
        df = import_data_same_folder(EXCEL_FILE, SHEET_RESULTS)

        col = {
            "func": find_column(df, ["Funcionário"]), "cpf": find_column(df, ["CPF"]),
            "cargo": find_column(df, ["Cargo"]), "unidade": find_column(df, ["Unidade"]),
            "empresa": find_column(df, ["Fornecedor"]), "regime": find_column(df, ["Regime"]),
            "dt_contratacao": find_column(df, ["Data de Contratação"]),
            "position": find_column(df, ["Position", "Função"], required=False)
        }
        if not col["position"]:
            print("AVISO: Coluna de 'Position'/'Função' não encontrada no Excel. Será usado o valor padrão 'N/A'.")

        df.dropna(subset=[col["func"]], inplace=True)
        df = df[df[col["func"]].str.strip() != '']
        df_unique = df.drop_duplicates(subset=[col["func"], col["empresa"]])
        print(f"\nEncontrados {len(df_unique)} funcionários únicos para processar.")

        ok, already, fail, skipped = 0, 0, 0, 0

        for idx, row in df_unique.iterrows():
            full_name = row[col["func"]].strip()
            cpf_val = str(row[col["cpf"]]).strip()
            cargo_excel = row[col["cargo"]].strip()
            unidade_excel = row[col["unidade"]].strip()
            empresa_excel = row[col["empresa"]].strip()

            # Normalização para busca
            unidade_norm = remove_accents(unidade_excel).lower()
            empresa_norm = remove_accents(empresa_excel).lower()

            warnings = []

            if not cpf_val or len(cpf_val) < 11:
                cpf_val = cpf.generate()

            first_name, surname = split_fullname(full_name)
            branch_id = branch_map.get(unidade_norm)

            cbo_id = None
            if cargo_excel:
                cargo_norm = remove_accents(cargo_excel).lower()
                cbo_id = cbo_map.get(cargo_norm)
                if not cbo_id:
                    try:
                        new_cbo_id = str(uuid.uuid4())
                        cursor.execute("INSERT INTO cbo (id, title, code) VALUES (%s, %s, NULL)",
                                       (new_cbo_id, cargo_excel))
                        conn.commit()
                        print(f"✔ CBO '{cargo_excel}' criado no banco.")
                        cbo_id = new_cbo_id
                        cbo_map[cargo_norm] = new_cbo_id
                    except mysql.connector.Error as err:
                        warnings.append(f"Falha ao criar CBO '{cargo_excel}': {err}")
                        cbo_id = None

            position_id = None
            if col["position"]:
                position_excel = row[col["position"]].strip()
                if position_excel:
                    position_norm = remove_accents(position_excel).lower()
                    position_id = position_map.get(position_norm)

            if not position_id:
                # Removido o log INFO daqui para não poluir a saída para cada linha
                position_id = default_position_id

            if not cbo_id: warnings.append(f"CBO (da coluna Cargo) '{cargo_excel}' não encontrado/criado")
            if not branch_id: warnings.append(f"Unidade '{unidade_excel}' não encontrada")

            supplier_id = supplier_map.get(empresa_norm)
            subcontract_id = None
            if not supplier_id:
                subcontract_id = subcontractor_map.get(empresa_norm)

            if not supplier_id and not subcontract_id:
                warnings.append(f"Empresa '{empresa_excel}' não encontrada")

            if warnings:
                print(f"↷ Ignorando '{full_name}': {'; '.join(warnings)}")
                skipped += 1
                continue

            payload = {"cpf": cpf_val, "name": first_name, "surname": surname, "positionId": position_id,
                       "branch": branch_id, "cboId": cbo_id, "admissionDate": to_date_sql(row[col["dt_contratacao"]]),
                       "contractType": normalize_text_enum(row[col["regime"]]) if row[col["regime"]] else None,
                       "supplier": supplier_id, "subcontract": subcontract_id, "situation": "DESALOCADO", "email": None,
                       "pis": None, "maritalStatus": None, "cep": None, "address": None, "country": "Brasil",
                       "acronym": "BR", "state": None, "birthDate": None, "city": None, "addressLine2": None,
                       "postalCode": None, "gender": None, "registration": None, "salary": None, "cellphone": None,
                       "platformAccess": "false", "telephone": None, "directory": None, "levelOfEducation": None,
                       "rg": None, "idContracts": [], "documents": []}

            try:
                r = requests.post(url, headers=headers, json=payload, timeout=REQ_TIMEOUT)
                if r.status_code in [200, 201]:
                    employee_data = r.json()
                    employee_id = employee_data.get('idEmployee')
                    # --- ALTERADO: Mensagem de log agora inclui a empresa ---
                    print(
                        f"✔ Funcionário '{full_name}' da empresa '{empresa_excel}' criado com sucesso com ID: {employee_id}")
                    ok += 1
                elif r.status_code == 409:
                    print(f"↷ Funcionário '{full_name}' (CPF: {cpf_val}) já existe.")
                    already += 1
                else:
                    print(f"✖ Falha ao criar '{full_name}' (Status: {r.status_code}): {r.text[:250]}")
                    fail += 1
            except requests.RequestException as e:
                print(f"✖ Exceção de rede ao criar '{full_name}': {e}")
                fail += 1

    finally:
        if conn and conn.is_connected():
            conn.close()
            print("Conexão com o banco de dados encerrada.")

    print_summary("Criação de Funcionários", ok, already, fail, skipped)


# ==============================================================================
# FUNÇÃO PENDENTE e EXECUÇÃO PRINCIPAL
# ==============================================================================
def assign_employees_to_contracts():
    print("\n------------------------------------------------------------")
    print("-> TODO: Implementar lógica de atribuição de funcionários a contratos.")
    print("------------------------------------------------------------")
    pass


if __name__ == "__main__":
    try:
        api_token = login()
        create_employees(api_token, CLIENT_ID)
        assign_employees_to_contracts()
    except Exception as e:
        print(f"\nOcorreu um erro inesperado na execução do script: {e}")