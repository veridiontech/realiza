#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import time
import hashlib
import pandas as pd
import requests
import mysql.connector  # NOVO: Import para conexão com o banco

# ==============================================================================
# CONFIGURAÇÕES GERAIS
# ==============================================================================
APP_URL = "http://localhost:8080".rstrip("/")

USER_LOGIN = {
    "email": "realiza@assessoria.com",
    "password": "senha123",
}

# ====== PREENCHA AQUI ======
EXCEL_FILE = "SISTEMA NOVO_ITAMINAS.xlsx"
SHEET_RESULTS = "Resultado da consulta"

# ID do cliente para buscar os dados do banco
CLIENT_ID = "8a291eb6-e627-4215-a622-3aa9fc39300e"
# ID do usuário que está fazendo a requisição do contrato
REQUESTER_ID = "fbbcae2f-8bd8-47a2-8049-6c5f98d2da91" # Exemplo, troque pelo ID correto

# NOVO: Configuração do banco de dados (verifique se está correta)
DB_CONFIG = {
    "host": "177.170.30.9",
    "port": 8004,
    "user": "veridion_user",
    "password": "SenhaSegura123!",
    "database": "dbrealiza"
}

# ====== Config de rede/log ======
REQ_TIMEOUT = 20

# ==============================================================================
# FUNÇÃO PARA BUSCAR MAPEAMENTOS NO BANCO DE DADOS (NOVO)
# ==============================================================================
def fetch_mappings_from_db(client_id: str) -> (dict, dict, dict):
    """
    Conecta ao banco de dados para buscar os mapeamentos de unidades,
    tipos de serviço e gestores relacionados a um cliente específico.
    """
    print("Buscando mapeamentos (unidades, serviços, gestores) no banco de dados...")
    conn = None
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()

        # 1. Buscar Unidades (Branches)
        cursor.execute("SELECT name, id_branch FROM branch WHERE id_client = %s", (client_id,))
        branch_map = {name: id_branch for name, id_branch in cursor.fetchall()}
        print(f"✔ {len(branch_map)} Unidades encontradas para o cliente.")

        # 2. Buscar Tipos de Serviço (vinculados às unidades do cliente)
        service_type_query = """
            SELECT DISTINCT st.title, st.id_service_type
            FROM service_type st
            JOIN service_type_branch stb ON st.id_service_type = stb.id_service_type
            JOIN branch b ON stb.id_branch = b.id_branch
            WHERE b.id_client = %s
        """
        cursor.execute(service_type_query, (client_id,))
        service_type_map = {title: id_service_type for title, id_service_type in cursor.fetchall()}
        print(f"✔ {len(service_type_map)} Tipos de Serviço encontrados.")

        # 3. Buscar Gestores (vinculados às unidades do cliente)
        manager_query = """
            SELECT DISTINCT au.email, au.id_user
            FROM app_user au
            JOIN user_client ub ON au.id_user = ub.id_user
            JOIN branch b ON ub.id_branch = b.id_branch
            WHERE b.id_client = %s AND au.role = 'ROLE_CLIENT_MANAGER' AND au.email IS NOT NULL
        """
        cursor.execute(manager_query, (client_id,))
        # Garante que a chave (email) seja minúscula para corresponder à lógica do script
        manager_map = {email.lower(): id_user for email, id_user in cursor.fetchall()}
        print(f"✔ {len(manager_map)} Gestores encontrados.")

        return branch_map, service_type_map, manager_map

    except mysql.connector.Error as err:
        print(f"✖✖ ERRO FATAL de banco de dados: {err}")
        raise SystemExit("Abortando devido à falha na busca de mapeamentos.")
    finally:
        if conn and conn.is_connected():
            conn.close()

# ==============================================================================
# HELPERS E FUNÇÕES EXISTENTES (sem alteração)
# ==============================================================================
def mask_email(email: str) -> str:
    if not email or "@" not in email: return str(email)
    name, dom = email.split("@", 1)
    return (name[:2] + "***@" + dom) if len(name) > 2 else ("***@" + dom)

def checksum(payload: dict) -> str:
    h = hashlib.sha1(repr(sorted(payload.items())).encode("utf-8")).hexdigest()
    return h[:10]

def print_summary(title: str, ok: int, already: int, fail: int, skipped: int = 0):
    print(f"\n{title} → criados: {ok} | existentes: {already} | falhas: {fail}" +
          (f" | ignorados: {skipped}" if skipped else ""))

def request_with_retry(session: requests.Session, method: str, url: str, **kwargs) -> requests.Response:
    # ... (código original sem alteração)
    attempts = 0
    while True:
        attempts += 1
        try:
            r = session.request(method, url, timeout=REQ_TIMEOUT, **kwargs)
            if r.status_code < 500 or attempts > 2:
                return r
            time.sleep(1.2 * attempts)
        except requests.RequestException as e:
            if attempts > 2: raise e
            time.sleep(1.2 * attempts)

def import_data_same_folder(file_name: str) -> pd.DataFrame:
    dir_atual = os.path.dirname(os.path.realpath(__file__))
    path = os.path.join(dir_atual, file_name)
    df = pd.read_excel(path, sheet_name=SHEET_RESULTS, engine="openpyxl", dtype=str, keep_default_na=False)
    print("Dados importados com sucesso!")
    return df

def find_column(df: pd.DataFrame, candidates):
    cols = {c.lower(): c for c in df.columns}
    for cand in candidates:
        if cand.lower() in cols: return cols[cand.lower()]
    for cand in candidates:
        for col in df.columns:
            if col.lower().startswith(cand.lower()): return col
    raise ValueError(f"Coluna não encontrada. Procure por um destes nomes: {candidates}")

def to_date_sql(value: str) -> str:
    if not value: return None
    try:
        d = pd.to_datetime(value, dayfirst=True, errors="coerce")
        return None if pd.isna(d) else d.strftime("%Y-%m-%d")
    except Exception:
        return None

# ==============================================================================
# LÓGICA DE NEGÓCIO (com alteração para usar MAPs dinâmicos)
# ==============================================================================
def build_contract_body(row, cols, warnings_list, branch_map, service_type_map, manager_map):
    provider_datas = {"corporateName": (row.get(cols["fornecedor"]) or "").strip() or None, "email": None, "cnpj": (row.get(cols["cnpj"]) or "").strip() or None, "telephone": None}

    unidade_nome = (row.get(cols["unidade"]) or "").strip()
    id_branch = branch_map.get(unidade_nome)
    if not id_branch: warnings_list.append(f"Branch não mapeada: '{unidade_nome}'")

    gestor_email = (row.get(cols["gestor_email"]) or "").strip().lower()
    if gestor_email:
        gestor_email = gestor_email.replace('@itaminas.com.br', '@teste.com.br')

    id_responsible = manager_map.get(gestor_email)
    if not id_responsible: warnings_list.append(f"Gestor não mapeado: '{gestor_email or '—'}'")

    tipo_servico_nome = (row.get(cols["tipo_servico"]) or "").strip()
    id_service_type = service_type_map.get(tipo_servico_nome)
    if not id_service_type: warnings_list.append(f"Tipo de serviço não mapeado: '{tipo_servico_nome}'")

    date_start = to_date_sql((row.get(cols["data_inicio"]) or "").strip())
    if not date_start: warnings_list.append(f"Data de Início inválida: '{row.get(cols['data_inicio'])}'")

    body = { "serviceName": (row.get(cols["servico"]) or "").strip() or None, "contractReference": (row.get(cols["referencia"]) or "").strip() or None, "description": None, "idResponsible": id_responsible, "expenseType": "OPEX", "labor": True, "hse": False, "dateStart": date_start, "idServiceType": id_service_type, "idRequester": REQUESTER_ID, "subcontractPermission": True, "idActivities": [], "idBranch": id_branch, "providerDatas": provider_datas, }
    return body


def create_contracts():
    url = f"{APP_URL}/contract/supplier"

    BRANCH_MAP, SERVICE_TYPE_MAP, MANAGER_MAP = fetch_mappings_from_db(CLIENT_ID)
    df = import_data_same_folder(EXCEL_FILE)

    col_map = {"tipo_contratacao": find_column(df, ["Tipo de Contratação"]), "unidade": find_column(df, ["Unidade"]),
               "fornecedor": find_column(df, ["Fornecedor"]), "cnpj": find_column(df, ["CNPJ"]),
               "servico": find_column(df, ["Serviço"]), "referencia": find_column(df, ["Referência"]),
               "gestor_email": find_column(df, ["E-mail Gestor"]), "tipo_servico": find_column(df, ["Tipo do Serviço"]),
               "data_inicio": find_column(df, ["Data de Início do Serviço"]), }

    df = df[df[col_map["tipo_contratacao"]].str.upper().str.strip() == "CONTRATADO"]
    seen_pairs, possibles = set(), []
    ok, already, fail, skipped = 0, 0, 0, 0

    token = login()
    with requests.Session() as s:
        s.headers.update({"Authorization": f"Bearer {token}", "Content-Type": "application/json"})

        for idx, row in df.iterrows():
            unidade = (row.get(col_map["unidade"]) or "").strip()
            fornecedor = (row.get(col_map["fornecedor"]) or "").strip()

            if not unidade or not fornecedor:
                skipped += 1
                continue

            if (unidade, fornecedor) in seen_pairs:
                skipped += 1
                continue
            seen_pairs.add((unidade, fornecedor))

            # --- LÓGICA ALTERADA AQUI ---
            warnings_list = []
            body = build_contract_body(row, col_map, warnings_list, BRANCH_MAP, SERVICE_TYPE_MAP, MANAGER_MAP)

            # Se a lista de avisos não estiver vazia, significa que um ID é nulo.
            # Então, pulamos a chamada da API para esta linha.
            if warnings_list:
                motivos = "; ".join(warnings_list)
                print(
                    f"↷ Linha {idx + 2} ignorada (mapeamento pendente): ({unidade} | {fornecedor}) | Motivos: {motivos}")
                skipped += 1
                continue  # Pula para a próxima linha do Excel
            # --- FIM DA ALTERAÇÃO ---

            try:
                r = request_with_retry(s, "POST", url, json=body)
                if r.status_code in (200, 201):
                    print(f"✔ contrato criado: ({unidade} | {fornecedor})")
                    ok += 1
                elif r.status_code == 409:
                    print(f"↷ contrato já existia (409): ({unidade} | {fornecedor})")
                    already += 1
                else:
                    print(f"✖ falha ({r.status_code}): ({unidade} | {fornecedor}) | resp={r.text[:220]}")
                    fail += 1
            except Exception as e:
                print(f"✖ exceção: ({unidade} | {fornecedor}): {e}")
                fail += 1

    print_summary("Contratos", ok, already, fail, skipped)

def login() -> str:
    url = f"{APP_URL}/login"
    r = requests.post(url, json=USER_LOGIN, timeout=REQ_TIMEOUT)
    r.raise_for_status()
    data = r.json()
    token = data.get("token") or data.get("access_token")
    if not token: raise RuntimeError(f"Login OK, mas não veio token. Resposta: {data}")
    print("Login ok")
    return token

if __name__ == "__main__":
    create_contracts()