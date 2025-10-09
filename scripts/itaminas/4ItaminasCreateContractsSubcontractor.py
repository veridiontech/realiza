#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import time
import hashlib
import unicodedata
import pandas as pd
import requests

APP_URL = "https://realiza.onrender.com".rstrip("/")

USER_LOGIN = {
    "email": "realiza@assessoria.com",
    "password": "senha123",
}

# ====== PREENCHA AQUI ======
EXCEL_FILE = "ITAMINAS_CONFIGURACOES.xlsx"     # na mesma pasta do script
SHEET_RESULTS = "Resultado da consulta"

REQUESTER_ID = "COLOQUE_ID_DO_REQUISITANTE"

# Map: CNPJ do FORNECEDOR PRINCIPAL -> idContractSupplier
SUPPLIER_CONTRACT_MAP = {
    # "12.345.678/0001-99": "ID_CONTRATO_SUPPLIER_ABC",
}

# ====== Config de rede/log ======
REQ_TIMEOUT = 20
RETRY_SLEEP_BASE = 1.2
MAX_RETRIES = 2

# ====== Helpers ======
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

def _strip_accents(s: str) -> str:
    return "".join(c for c in unicodedata.normalize("NFD", s) if unicodedata.category(c) != "Mn")

def find_column(df: pd.DataFrame, candidates):
    """
    Busca coluna ignorando maiúsculas/minúsculas e acentos.
    Também tenta por 'startswith'.
    """
    norm_map = { _strip_accents(c.lower()): c for c in df.columns }
    for cand in candidates:
        key = _strip_accents(cand.lower())
        if key in norm_map:
            return norm_map[key]
    # fallback: startswith
    for cand in candidates:
        key = _strip_accents(cand.lower())
        for c in df.columns:
            if _strip_accents(c.lower()).startswith(key):
                return c
    raise ValueError(f"Coluna não encontrada. Procure por: {candidates}")

def to_date_sql(value: str) -> str:
    if not value:
        return None
    try:
        d = pd.to_datetime(value, dayfirst=True, errors="coerce")
        if pd.isna(d):
            return None
        return d.strftime("%Y-%m-%d")
    except Exception:
        return None

# ====== Auth ======
def login() -> str:
    url = f"{APP_URL}/login"
    r = requests.post(url, json=USER_LOGIN, timeout=REQ_TIMEOUT)
    r.raise_for_status()
    data = r.json()
    token = data.get("token") or data.get("access_token")
    if not token:
        raise RuntimeError(f"Login OK, mas não veio token. Resposta: {data}")
    print("Login ok")
    return token

# ====== Build body do Subcontract ======
def build_subcontract_body(row, cols, warnings_list):
    """
    mapeamentos pedidos:
    - providerDatas.corporateName <- Subcontratado
    - providerDatas.email <- null
    - providerDatas.cnpj <- Subcontratado CNPJ
    - providerDatas.telephone <- null
    - idContractSupplier <- SUPPLIER_CONTRACT_MAP[CNPJ (do FORNECEDOR principal)]
    - serviceName <- Serviço
    - contractReference <- Referência
    - expenseType <- "OPEX"
    - labor <- True
    - hse <- False
    - dateStart <- Data de Início do Serviço (YYYY-MM-DD)
    - idRequester <- REQUESTER_ID
    - idActivities <- []
    - description <- null
    """
    # providerDatas
    provider_datas = {
        "corporateName": (row.get(cols["subcontratado"]) or "").strip() or None,
        "email": None,
        "cnpj": (row.get(cols["subcontratado_cnpj"]) or "").strip() or None,
        "telephone": None,
    }

    # idContractSupplier
    supplier_cnpj = (row.get(cols["cnpj_fornecedor"]) or "").strip()
    id_contract_supplier = SUPPLIER_CONTRACT_MAP.get(supplier_cnpj)
    if not id_contract_supplier:
        warnings_list.append(f"idContractSupplier não mapeado para CNPJ do fornecedor: '{supplier_cnpj or '—'}'")

    # Datas
    date_start = to_date_sql((row.get(cols["data_inicio"]) or "").strip())
    if not date_start:
        warnings_list.append(f"Data de Início inválida: '{row.get(cols['data_inicio'])}'")

    body = {
        "serviceName": (row.get(cols["servico"]) or "").strip() or None,
        "contractReference": (row.get(cols["referencia"]) or "").strip() or None,
        "description": None,
        "expenseType": "OPEX",
        "labor": True,
        "hse": False,
        "dateStart": date_start,
        "idRequester": REQUESTER_ID,
        "idActivities": [],
        "idContractSupplier": id_contract_supplier,
        "providerDatas": provider_datas,
    }
    return body

# ====== Execução principal ======
def create_subcontracts():
    url = f"{APP_URL}/contract/subcontractor"

    df = import_data_same_folder(EXCEL_FILE)

    # descobre colunas (case/acentos-insensitive)
    col_tipo_contratacao = find_column(df, ["Tipo de Contratação", "Tipo de Contratacao"])
    col_unidade          = find_column(df, ["Unidade", "Unidade*"])
    col_fornecedor       = find_column(df, ["Fornecedor"])
    col_cnpj_fornec      = find_column(df, ["CNPJ", "CNPJ*"])  # CNPJ do fornecedor principal
    col_servico          = find_column(df, ["Serviço", "Servico"])
    col_referencia       = find_column(df, ["Referência", "Referencia"])
    col_data_inicio      = find_column(df, ["Data de Início do Serviço", "Data de Inicio do Servico"])
    col_subcontratado    = find_column(df, ["Subcontratado"])
    col_subcontratado_cnpj = find_column(df, ["Subcontratado CNPJ", "CNPJ Subcontratado"])

    # filtra somente Subcontratado
    df = df[df[col_tipo_contratacao].str.upper().str.strip() == "SUBCONTRATADO"]

    # deduplicar por (Unidade, Subcontratado)
    seen = set()

    ok = already = fail = skipped = 0
    possibles = []  # pendências de mapeamento/data etc.

    token = login()
    with requests.Session() as s:
        s.headers.update({
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json",
        })

        for idx, row in df.iterrows():
            unidade = (row.get(col_unidade) or "").strip()
            subc_nome = (row.get(col_subcontratado) or "").strip()

            if not unidade or not subc_nome:
                skipped += 1
                print(f"↷ linha {idx} ignorada: Unidade/Subcontratado vazio.")
                continue

            key = (unidade, subc_nome)
            if key in seen:
                skipped += 1
                print(f"↷ linha {idx} duplicada para (Unidade='{unidade}', Subcontratado='{subc_nome}') — ignorado.")
                continue
            seen.add(key)

            warnings_list = []
            cols = {
                "servico": col_servico,
                "referencia": col_referencia,
                "data_inicio": col_data_inicio,
                "cnpj_fornecedor": col_cnpj_fornec,    # para SUPPLIER_CONTRACT_MAP
                "subcontratado": col_subcontratado,
                "subcontratado_cnpj": col_subcontratado_cnpj,
                "cnpj_fornecedor": col_cnpj_fornec,
            }
            body = build_subcontract_body(row, cols, warnings_list)
            chk = checksum(body)
            started = time.time()

            if warnings_list:
                possibles.append({
                    "unidade": unidade,
                    "subcontratado": subc_nome,
                    "motivos": warnings_list,
                    "ref": row.get(col_referencia),
                })

            try:
                r = request_with_retry(s, "POST", url, json=body)
                ms = round((time.time() - started) * 1000)

                if r.status_code in (200, 201):
                    rid = None
                    if r.headers.get("Content-Type", "").startswith("application/json"):
                        rid = (r.json() or {}).get("id")
                    print(f"✔ subcontrato criado: ({unidade} | {subc_nome}) -> id={rid or '—'} | chk={chk} | {ms}ms")
                    ok += 1
                elif r.status_code == 409:
                    print(f"↷ subcontrato já existia (409): ({unidade} | {subc_nome}) | chk={chk}")
                    already += 1
                else:
                    print(f"✖ falha criar subcontrato ({r.status_code}): ({unidade} | {subc_nome}) | resp={r.text[:220]} | chk={chk}")
                    fail += 1

            except Exception as e:
                print(f"✖ exceção ao criar subcontrato ({unidade} | {subc_nome}): {e} | chk={chk}")
                fail += 1

    print_summary("Subcontratos", ok, already, fail, skipped)

    if possibles:
        print("\nATENÇÃO — Itens com potenciais problemas:")
        for p in possibles:
            motivos = "; ".join(p["motivos"])
            print(f"• Unidade='{p['unidade']}' | Subcontratado='{p['subcontratado']}' | Ref='{p.get('ref')}' | Motivos: {motivos}")
    else:
        print("\nSem pendências de mapeamento/data detectadas.")

if __name__ == "__main__":
    create_subcontracts()
