#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import time
import json
import hashlib
import logging
from typing import Dict, Any, Optional, List, Tuple

import pandas as pd
import requests

# ==============================================================================
# CONFIG
# ==============================================================================

APP_URL = "https://realiza.onrender.com".rstrip("/" )

USER_LOGIN = {
    "email": "realiza@assessoria.com",
    "password": "senha123",
}

# IDs do cliente/perfil
CLIENT_GLOBAL_ID = "57a731ee-6deb-440a-bc69-c0b59b38b3c0" 
DEFAULT_PROFILE_NAME = "Padrão"

# Arquivos de dados
CONTRACT_CSV = "contract.csv"
BRANCH_CSV = "Branch.csv"
USER_XLSX = "SISTEMA NOVO_ITAMINAS.xlsx" # Para Tipos de Serviço

# Timeouts (conexão, leitura)
CONNECT_TIMEOUT = 10
READ_TIMEOUT = 60

logging.basicConfig(level=logging.INFO, format="%(asctime)s | %(levelname)s | %(message)s")
log = logging.getLogger("realiza.setup")

# ==============================================================================
# Helpers
# ==============================================================================

def mask_email(email: str) -> str:
    # ... (função mantida)
    if not email or "@" not in email:
        return str(email)
    name, dom = email.split("@", 1)
    return (name[:2] + "***@" + dom) if len(name) > 2 else ("***@" + dom)

def find_col(df: pd.DataFrame, candidates: List[str]) -> str:
    # ... (função mantida)
    norm = {c.lower(): c for c in df.columns}
    for cand in candidates:
        lc = cand.lower()
        if lc in norm:
            return norm[lc]
    for cand in candidates:
        lc = cand.lower()
        for c in df.columns:
            if c.lower().startswith(lc):
                return c
    raise KeyError(f"Coluna não encontrada. Tente uma destas: {candidates}")

def nonempty(v: Any) -> Optional[str]:
    # ... (função mantida)
    if v is None:
        return None
    s = str(v).strip()
    return s if s else None

# ==============================================================================
# HTTP (warmup, login, retry)
# ==============================================================================

def login() -> str:
    paths = ["/api/auth/login", "/login", "/api/login"]
    with requests.Session() as s:
        for p in paths:
            try:
                r = s.post(f"{APP_URL}{p}", json=USER_LOGIN, timeout=(CONNECT_TIMEOUT, READ_TIMEOUT))
                if r.status_code >= 400: continue
                data = r.json() if r.content else {}
                token = data.get("token") or data.get("access_token") or data.get("jwt")
                if token:
                    log.info(f"✔ Login OK via {p} — usuário {mask_email(USER_LOGIN['email'])}")
                    return token
            except requests.RequestException as e:
                log.info(f"✖ Login via {p} falhou: {e}")
        raise RuntimeError("Não foi possível autenticar.")

def request_with_retry(session: requests.Session, method: str, url: str, **kwargs) -> requests.Response:
    max_retries = kwargs.pop("max_retries", 3)
    timeout = kwargs.pop("timeout", (CONNECT_TIMEOUT, READ_TIMEOUT))
    attempt = 0
    last_err = None
    while attempt < max_retries:
        attempt += 1
        try:
            r = session.request(method, url, timeout=timeout, **kwargs)
            if r.status_code < 500:
                return r
            log.warning(f"HTTP {r.status_code} em {url}. Tentativa {attempt}/{max_retries}.")
        except requests.RequestException as e:
            last_err = e
            log.warning(f"Erro de rede em {url}: {e}. Tentativa {attempt}/{max_retries}.")
        time.sleep(1.2 * attempt)
    if last_err:
        raise last_err
    return r

# ==============================================================================
# Leitura CSV/Excel
# ==============================================================================

def load_csv(path: str) -> pd.DataFrame:
    base = os.path.dirname(os.path.realpath(__file__))
    full = os.path.join(base, path)
    try:
        df = pd.read_csv(full, dtype=str, keep_default_na=False, encoding='utf-8')
        return df
    except Exception as e:
        raise RuntimeError(f"✖ Erro ao ler {path}: {e}")

def load_excel_sheet(path: str, sheet_name: str) -> pd.DataFrame:
    # ... (função mantida)
    base = os.path.dirname(os.path.realpath(__file__))
    full = os.path.join(base, path)
    try:
        df = pd.read_excel(full, sheet_name=sheet_name, engine="openpyxl", dtype=str, keep_default_na=False)
        return df
    except Exception as e:
        log.warning(f"✖ Aba '{sheet_name}' não encontrada ou erro ao ler: {e}. Retornando DataFrame vazio.")
        return pd.DataFrame()

# ==============================================================================
# Mapeamento de Dados (Busca no Banco)
# ==============================================================================

def fetch_mappings_from_db(client_id: str, token: str) -> Tuple[Dict, Dict, Dict, Dict, Dict]:
    # Esta função simula a busca de IDs no banco de dados (API)
    # Na prática, você precisará de endpoints para buscar branches, service types e managers
    # Como não temos os endpoints, vamos simular que o Script 02 criou alguns dados
    
    # Simulação de dados que o Script 02 criou
    MANAGER_MAP: Dict[str, str] = {
        "joao.rezende@itaminas.com.br": "id_joao",
        "delano.noronha@itaminas.com.br": "id_delano",
        "heverton.paula@itaminas.com.br": "id_heverton",
        "realiza@assessoria.com": "id_realiza",
        # ... Adicione mais gestores que você sabe que foram criados
    }
    
    # Simulação de dados que o Script 01 criou
    SERVICE_TYPE_MAP_N: Dict[str, str] = {
        "Limpeza e Jardinagem": "id_limpeza",
        "Mineração": "id_mineracao",
        # ... Adicione mais tipos de serviço
    }
    
    # O Script 03 usará o Branch.csv para o mapeamento de Filiais
    BRANCH_MAP_N: Dict[str, str] = {} # Não usado aqui
    BRANCH_NAMES: Dict[str, str] = {} # Não usado aqui
    SERVICE_NAMES: Dict[str, str] = {} # Não usado aqui

    return BRANCH_MAP_N, SERVICE_TYPE_MAP_N, MANAGER_MAP, BRANCH_NAMES, SERVICE_NAMES


# ==============================================================================
# Contratos
# ==============================================================================

def create_contracts():
    token = login()
    session = requests.Session()
    
    # Mapeamentos do Banco (Gestores e Tipos de Serviço)
    BRANCH_MAP_N, SERVICE_TYPE_MAP_N, MANAGER_MAP, BRANCH_NAMES, SERVICE_NAMES = fetch_mappings_from_db(CLIENT_GLOBAL_ID, token)

    # 1. Mapeamento de Filiais (Branches) a partir do CSV
    branch_df = load_csv(BRANCH_CSV)
    BRANCH_MAP_CODIFICADO: Dict[str, str] = {}
    try:
        for idx, row in branch_df.iterrows():
            BRANCH_MAP_CODIFICADO[row['name']] = row['idBranch']
    except KeyError:
        log.error("✖ CSV de Branches faltando colunas 'name' ou 'idBranch'.")
        return

    # 2. Leitura dos Contratos a serem criados
    df_contracts = load_csv(CONTRACT_CSV)
    
    # Colunas necessárias (assumindo que o contract.csv tem as colunas do Excel)
    try:
        unit_col = find_col(df_contracts, ["Unidade*", "Unidade"])
        cnpj_col = find_col(df_contracts, ["CNPJ*", "CNPJ"])
        service_col = find_col(df_contracts, ["Serviço*", "Serviço"])
        manager_col = find_col(df_contracts, ["E-mail Gestor", "Email Gestor"])
        ref_col = find_col(df_contracts, ["Referência", "Ref"])
    except KeyError as e:
        log.error(f"✖ CSV de Contratos faltando coluna essencial: {e}")
        return

    created = 0
    fail = 0
    
    for idx, row in df_contracts.iterrows():
        unit_name = nonempty(row.get(unit_col))
        cnpj = nonempty(row.get(cnpj_col))
        service_name = nonempty(row.get(service_col))
        manager_email = nonempty(row.get(manager_col))
        ref = nonempty(row.get(ref_col))
        
        # 3. Validação e Mapeamento de IDs
        
        # Mapeamento de Filial (Branch)
        branch_id = BRANCH_MAP_CODIFICADO.get(unit_name)
        if not branch_id:
            log.warning(f"AVISO (linha {idx+2}): Filial '{unit_name}' não mapeada. Contrato '{ref}' ignorado.")
            fail += 1
            continue

        # Mapeamento de Gestor
        manager_id = MANAGER_MAP.get(manager_email)
        if not manager_id:
            log.warning(f"AVISO (linha {idx+2}): Gestor '{manager_email}' não encontrado no banco. Contrato '{ref}' ignorado.")
            fail += 1
            continue
            
        # Mapeamento de Tipo de Serviço
        service_type_id = SERVICE_TYPE_MAP_N.get(service_name)
        if not service_type_id:
            log.warning(f"AVISO (linha {idx+2}): Tipo de Serviço '{service_name}' não mapeado. Contrato '{ref}' ignorado.")
            fail += 1
            continue
            
        # 4. Payload e Criação
        
        payload = {
            "branch": branch_id,
            "cnpj": cnpj,
            "serviceType": service_type_id,
            "manager": manager_id,
            "reference": ref,
            "client": CLIENT_GLOBAL_ID,
            # ... adicione outros campos necessários para a criação do contrato
        }
        
        # Tenta criar o contrato
        path_opts = ["/api/contract", "/contract"]
        ok_this = False
        for p in path_opts:
            url = f"{APP_URL}{p}"
            try:
                r = request_with_retry(session, "POST", url, headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"}, json=payload)
                
                if r.status_code in (200, 201):
                    log.info(f"✔ Contrato criado: {ref} | Filial={unit_name}")
                    created += 1
                    ok_this = True
                    break
                
                if r.status_code == 409:
                    log.info(f"↷ Contrato já existia: {ref}")
                    ok_this = True
                    break
            except Exception as e:
                last = str(e)
                continue
        
        if not ok_this:
            log.error(f"✖ Falha ao criar contrato '{ref}' | erro={last if 'last' in locals() else 'desconhecido'}")
            fail += 1

    print(f"\nResumo Contratos → criados: {created} | existentes: {fail} | falhas: {fail}")


# ==============================================================================
# Main
# ==============================================================================

if __name__ == "__main__":
    try:
        create_contracts()
    except Exception as e:
        log.error(f"Ocorreu um erro inesperado na execução do script: {e}")
