#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import requests
import mysql.connector
import pandas as pd
from collections import defaultdict

# ==============================================================================
# CONFIGURAÇÕES GERAIS
# ==============================================================================
APP_URL = "https://realiza.onrender.com".rstrip("/")
EXCEL_FILE = "SISTEMA NOVO_ITAMINAS.xlsx"
SHEET_RESULTS = "Resultado da consulta"
CLIENT_ID = "d2bd8165-95ac-4d11-9e97-968979b9bc5f"

USER_LOGIN = {"email": "realiza@assessoria.com", "password": "senha123"}
DB_CONFIG = {
    "host": "177.170.30.9", "port": 8004, "user": "veridion_user",
    "password": "SenhaSegura123!", "database": "dbrealiza"
}
REQ_TIMEOUT = 30


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


def print_summary(title: str, ok: int, fail: int, skipped: int):
    print(f"\n{title} → Sucessos: {ok} | Falhas: {fail} | Linhas ignoradas (dados não encontrados): {skipped}")


def find_column(df: pd.DataFrame, candidates, required=True):
    cols = {c.lower().strip(): c for c in df.columns}
    for cand in candidates:
        if cand.lower() in cols: return cols[cand.lower()]
    if required:
        raise ValueError(f"Coluna obrigatória não encontrada: {candidates}")
    return None


# ==============================================================================
# LÓGICA PRINCIPAL
# ==============================================================================
def assign_employees_to_contracts(token: str, client_id: str):
    print("\nIniciando processo de atribuição de funcionários a contratos...")

    employee_map = {}
    contract_map = {}
    conn = None

    # --- 1. BUSCAR MAPAS DE TRADUÇÃO DO BANCO (CPF -> ID, Referência -> ID) ---
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("✔ Conectado ao banco para buscar mapas de IDs.")

        # Mapa de Funcionários: CPF -> ID
        cursor.execute("""
            SELECT eb.cpf, e.id_employee 
            FROM employee e 
            JOIN employee_brazilian eb ON e.id_employee = eb.id_employee
        """)
        employee_map = {cpf: id_employee for cpf, id_employee in cursor.fetchall()}
        print(f"✔ {len(employee_map)} funcionários carregados do banco.")

        # Mapa de Contratos: Referência -> ID
        # Filtra contratos pelo cliente para garantir que estamos no escopo correto
        cursor.execute("""
            SELECT c.contract_reference, c.id_contract 
            FROM contract c
            JOIN contract_provider_supplier cps ON c.id_contract = cps.id_contract
            JOIN branch b ON cps.id_branch = b.id_branch
            WHERE b.id_client = %s
        """, (client_id,))
        contract_map = {ref: id_contract for ref, id_contract in cursor.fetchall()}
        print(f"✔ {len(contract_map)} contratos do cliente carregados do banco.")

    except mysql.connector.Error as err:
        print(f"✖ ERRO FATAL de banco de dados: {err}")
        return
    finally:
        if conn and conn.is_connected(): conn.close()

    # --- 2. LER O EXCEL E AGRUPAR FUNCIONÁRIOS POR CONTRATO ---
    df = pd.read_excel(os.path.join(os.path.dirname(__file__), EXCEL_FILE), sheet_name=SHEET_RESULTS, dtype=str,
                       keep_default_na=False)

    col = {
        "cpf": find_column(df, ["CPF"]),
        "ref": find_column(df, ["Referência"])
    }

    contract_id_to_employees = defaultdict(list)
    skipped_rows = 0

    print("\nProcessando planilha Excel para agrupar funcionários por contrato...")
    for idx, row in df.iterrows():
        cpf_val = str(row[col["cpf"]]).strip()
        ref_val = str(row[col["ref"]]).strip()

        if not cpf_val or not ref_val:
            skipped_rows += 1
            continue

        employee_id = employee_map.get(cpf_val)
        contract_id = contract_map.get(ref_val)

        if employee_id and contract_id:
            # Adiciona apenas se não estiver na lista, para evitar duplicatas
            if employee_id not in contract_id_to_employees[contract_id]:
                contract_id_to_employees[contract_id].append(employee_id)
        else:
            if not employee_id: print(
                f"  -> AVISO (linha {idx + 2}): Funcionário com CPF {cpf_val} não encontrado no banco.")
            if not contract_id: print(
                f"  -> AVISO (linha {idx + 2}): Contrato com referência '{ref_val}' não encontrado no banco.")
            skipped_rows += 1

    print(f"✔ Planilha processada. {len(contract_id_to_employees)} contratos serão atualizados.")

    # --- 3. FAZER AS CHAMADAS À API ---
    ok, fail = 0, 0
    with requests.Session() as s:
        s.headers.update({"Authorization": f"Bearer {token}", "Content-Type": "application/json"})

        print("\nIniciando chamadas à API para adicionar funcionários aos contratos...")

        for contract_id, employee_ids in contract_id_to_employees.items():
            url = f"{APP_URL}/contract/add-employee/{contract_id}"
            payload = {"employees": employee_ids}

            # Pega a referência de volta para o log
            ref_for_log = next((ref for ref, c_id in contract_map.items() if c_id == contract_id), "Ref. Desconhecida")

            try:
                r = s.post(url, json=payload, timeout=REQ_TIMEOUT)
                if r.status_code == 200:
                    print(
                        f"✔ Sucesso ao adicionar {len(employee_ids)} funcionários ao contrato {ref_for_log} ({contract_id[:8]}...).")
                    ok += 1
                else:
                    print(
                        f"✖ Falha ao adicionar funcionários ao contrato {ref_for_log} (Status: {r.status_code}): {r.text[:200]}")
                    fail += 1
            except requests.RequestException as e:
                print(f"✖ Exceção de rede no contrato {ref_for_log}: {e}")
                fail += 1

    print_summary("Resumo da Atribuição", ok, fail, skipped_rows)


# ==============================================================================
# EXECUÇÃO PRINCIPAL
# ==============================================================================
if __name__ == "__main__":
    try:
        api_token = login()
        assign_employees_to_contracts(api_token, CLIENT_ID)
    except Exception as e:
        print(f"\nOcorreu um erro inesperado na execução do script: {e}")