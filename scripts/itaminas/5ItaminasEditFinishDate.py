#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import time
import hashlib
import pandas as pd
import requests

APP_URL = "https://realiza.onrender.com".rstrip("/")

# ====== PREENCHA AQUI ======
EXCEL_FILE = "SISTEMA NOVO_ITAMINAS.xlsx"  # na mesma pasta do script
SHEET_RESULTS = "Resultado da consulta"

CLIENT_ID = "d2bd8165-95ac-4d11-9e97-968979b9bc5f"  # caso precise
REQUESTER_ID = "0eef7b0b-e2cf-4bd8-b799-9ecc21c5d1df"

USER_LOGIN = {
    "email": "realiza@assessoria.com",    # <-- Substitua pelo seu email
    "password": "senha123", # <-- Substitua pela sua senha
}

# Dicionários fornecidos por você:
BRANCH_MAP = {
    # "Unidade X": "idBranchX",
}
SERVICE_TYPE_MAP = {
    # "Tipo Serviço Y": "idServiceTypeY",
}
MANAGER_MAP = {
    # "gestor@empresa.com": "idResponsibleZ",
}

# ====== Config de rede/log ======
REQ_TIMEOUT = 20
RETRY_SLEEP_BASE = 1.2
MAX_RETRIES = 2

# ====== Helpers de log ======
def mask_email(email: str) -> str:
    if not email or "@" not in email:
        return str(email)
    name, dom = email.split("@", 1)
    return (name[:2] + "***@" + dom) if len(name) > 2 else ("***@" + dom)

def checksum(payload: dict) -> str:
    h = hashlib.sha1(repr(sorted(payload.items())).encode("utf-8")).hexdigest()
    return h[:10]

def print_summary(title: str, ok: int, already: int, fail: int, skipped: int = 0):
    print(f"\n{title} → criados: {ok} | existentes: {already} | falhas: {fail}" +
          (f" | ignorados: {skipped}" if skipped else ""))

def request_with_retry(session: requests.Session, method: str, url: str, **kwargs) -> requests.Response:
    attempts = 0
    while True:
        attempts += 1
        try:
            r = session.request(method, url, timeout=REQ_TIMEOUT, **kwargs)
        except requests.RequestException as e:
            if attempts <= MAX_RETRIES:
                sleep = RETRY_SLEEP_BASE * attempts
                print(f"↻ exceção de rede, retry em {sleep:.1f}s… | err={e}")
                time.sleep(sleep)
                continue
            raise
        if r.status_code in (429, 502, 503, 504) and attempts <= MAX_RETRIES:
            sleep = RETRY_SLEEP_BASE * attempts
            ra = r.headers.get("Retry-After")
            if ra:
                try:
                    sleep = max(sleep, float(ra))
                except ValueError:
                    pass
            print(f"↻ {r.status_code} em {url} | retry em {sleep:.1f}s…")
            time.sleep(sleep)
            continue
        return r

def import_data_same_folder(file_name: str) -> pd.DataFrame:
    dir_atual = os.path.dirname(os.path.realpath(__file__))
    path = os.path.join(dir_atual, file_name)
    df = pd.read_excel(
        path,
        sheet_name=SHEET_RESULTS,
        engine="openpyxl",
        dtype=str,
        keep_default_na=False
    )
    print("Dados importados com sucesso!")
    return df

# ====== Coluna → campo (robusto a variação de maiúsculas/acentos) ======
def find_column(df: pd.DataFrame, candidates):
    cols = {c.lower(): c for c in df.columns}
    for cand in candidates:
        if cand.lower() in cols:
            return cols[cand.lower()]
    # tentativa: procurar por startswith aproximado
    for cand in candidates:
        for col in df.columns:
            if col.lower().startswith(cand.lower()):
                return col
    raise ValueError(f"Coluna não encontrada. Procure por um destes nomes: {candidates}")

# ====== Normalizações pontuais ======
def to_date_sql(value: str) -> str:
    """
    Converte para 'YYYY-MM-DD' (Date SQL). Retorna None se não conseguir.
    """
    if not value:
        return None
    try:
        d = pd.to_datetime(value, dayfirst=True, errors="coerce")
        if pd.isna(d):
            return None
        return d.strftime("%Y-%m-%d")
    except Exception:
        return None

# ====== Corpo do contrato ======
def build_contract_body(row, cols, warnings_list):
    # ProviderDatas
    provider_datas = {
        "corporateName": (row.get(cols["fornecedor"]) or "").strip() or None,
        "email": None,   # sempre null
        "cnpj": (row.get(cols["cnpj"]) or "").strip() or None,
        "telephone": None,  # sempre null
    }

    # Branch
    unidade_nome = (row.get(cols["unidade"]) or "").strip()
    id_branch = BRANCH_MAP.get(unidade_nome)
    if not id_branch:
        warnings_list.append(f"Branch não mapeada: '{unidade_nome}'")

    # Gestor
    gestor_email = (row.get(cols["gestor_email"]) or "").strip().lower()
    id_responsible = MANAGER_MAP.get(gestor_email)
    if not id_responsible:
        warnings_list.append(f"Gestor não mapeado: '{gestor_email or '—'}'")

    # Tipo de serviço
    tipo_servico_nome = (row.get(cols["tipo_servico"]) or "").strip()
    id_service_type = SERVICE_TYPE_MAP.get(tipo_servico_nome)
    if not id_service_type:
        warnings_list.append(f"Tipo de serviço não mapeado: '{tipo_servico_nome}'")

    # Datas
    date_start = to_date_sql((row.get(cols["data_inicio"]) or "").strip())
    if not date_start:
        warnings_list.append(f"Data de Início inválida: '{row.get(cols['data_inicio'])}'")

    # Demais campos diretos
    service_name = (row.get(cols["servico"]) or "").strip() or None
    contract_ref = (row.get(cols["referencia"]) or "").strip() or None
    description = None  # não informado
    id_requester = REQUESTER_ID
    expense_type = "OPEX"        # sempre OPEX
    labor = True                 # sempre true
    hse = False                  # sempre false
    subcontract_permission = True
    id_activities = []           # lista vazia (não informado)

    body = {
        "serviceName": service_name,
        "contractReference": contract_ref,
        "description": description,
        "idResponsible": id_responsible,
        "expenseType": expense_type,
        "labor": labor,
        "hse": hse,
        "dateStart": date_start,
        "idServiceType": id_service_type,
        "idRequester": id_requester,
        "subcontractPermission": subcontract_permission,
        "idActivities": id_activities,
        "idBranch": id_branch,
        "providerDatas": provider_datas,
    }
    return body

# ====== Execução ======
def create_contracts():
    url = f"{APP_URL}/contract/supplier"

    df = import_data_same_folder(EXCEL_FILE)

    # Descobrir nomes de colunas relevantes (flexível)
    col_tipo_contratacao = find_column(df, ["Tipo de Contratação", "Tipo de Contratacao"])
    col_unidade          = find_column(df, ["Unidade", "Unidade*"])
    col_fornecedor       = find_column(df, ["Fornecedor"])
    col_cnpj             = find_column(df, ["CNPJ", "CNPJ*"])
    col_servico          = find_column(df, ["Serviço", "Servico"])
    col_referencia       = find_column(df, ["Referência", "Referencia"])
    col_gestor_email     = find_column(df, ["E-mail Gestor", "Email Gestor", "Gestor E-mail"])
    col_tipo_servico     = find_column(df, ["Tipo do Serviço", "Tipo do Servico"])
    col_data_inicio      = find_column(df, ["Data de Início do Serviço", "Data de Inicio do Servico"])

    # Filtra somente "Contratado"
    df = df[df[col_tipo_contratacao].str.upper().str.strip() == "CONTRATADO"]

    # Deduplicar por (Unidade, Fornecedor)
    seen_pairs = set()

    ok = already = fail = skipped = 0
    possibles = []  # lista de casos com potenciais problemas (mapeamentos faltando, datas ruins, etc.)

    # Sessão + auth
    token = login()
    with requests.Session() as s:
        s.headers.update({
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json",
        })

        # Itera linhas
        for idx, row in df.iterrows():
            unidade = (row.get(col_unidade) or "").strip()
            fornecedor = (row.get(col_fornecedor) or "").strip()
            if not unidade or not fornecedor:
                skipped += 1
                print(f"↷ linha {idx} ignorada: Unidade/Fornecedor vazio.")
                continue

            pair_key = (unidade, fornecedor)
            if pair_key in seen_pairs:
                skipped += 1
                print(f"↷ linha {idx} duplicada para par (Unidade='{unidade}', Fornecedor='{fornecedor}') — ignorado.")
                continue
            seen_pairs.add(pair_key)

            # Monta body
            warnings_list = []
            cols = {
                "unidade": col_unidade,
                "fornecedor": col_fornecedor,
                "cnpj": col_cnpj,
                "servico": col_servico,
                "referencia": col_referencia,  # “primeira coluna Referência” — aqui usamos a detectada
                "gestor_email": col_gestor_email,
                "tipo_servico": col_tipo_servico,
                "data_inicio": col_data_inicio,
            }
            body = build_contract_body(row, cols, warnings_list)
            chk = checksum(body)
            started = time.time()

            # Se houver avisos de mapeamento faltante, registra (mas segue)
            if warnings_list:
                possibles.append({
                    "unidade": unidade,
                    "fornecedor": fornecedor,
                    "motivos": warnings_list,
                    "ref": row.get(col_referencia),
                    "gestor_email": row.get(col_gestor_email),
                })

            # POST
            try:
                r = request_with_retry(s, "POST", url, json=body)
                ms = round((time.time() - started) * 1000)

                if r.status_code in (200, 201):
                    rid = None
                    if r.headers.get("Content-Type", "").startswith("application/json"):
                        rid = (r.json() or {}).get("id")
                    print(f"✔ contrato criado: ({unidade} | {fornecedor}) -> id={rid or '—'} | chk={chk} | {ms}ms")
                    ok += 1
                elif r.status_code == 409:
                    print(f"↷ contrato já existia (409): ({unidade} | {fornecedor}) | chk={chk}")
                    already += 1
                else:
                    print(f"✖ falha criar contrato ({r.status_code}): ({unidade} | {fornecedor}) | resp={r.text[:220]} | chk={chk}")
                    fail += 1
            except Exception as e:
                print(f"✖ exceção ao criar contrato ({unidade} | {fornecedor}): {e} | chk={chk}")
                fail += 1

    print_summary("Contratos", ok, already, fail, skipped)

    # Relatório de potenciais problemáticos
    if possibles:
        print("\nATENÇÃO — Itens com potenciais problemas (mapeamentos/data):")
        for p in possibles:
            motivos = "; ".join(p["motivos"])
            print(f"• Unidade='{p['unidade']}' | Fornecedor='{p['fornecedor']}' | Ref='{p.get('ref')}' | Gestor='{mask_email(p.get('gestor_email') or '')}' | Motivos: {motivos}")
    else:
        print("\nSem pendências de mapeamento/data detectadas.")

# ====== Auth simples (igual aos scripts anteriores) ======
def login() -> str:
    url = f"{APP_URL}/login"
    r = requests.post(url, json={"email": USER_LOGIN["email"], "password": USER_LOGIN["password"]}, timeout=REQ_TIMEOUT)
    r.raise_for_status()
    data = r.json()
    token = data.get("token") or data.get("access_token")
    if not token:
        raise RuntimeError(f"Login OK, mas não veio token. Resposta: {data}")
    print("Login ok")
    return token

if __name__ == "__main__":
    create_contracts()
