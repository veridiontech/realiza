#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import time
import json
import hashlib
import logging
from typing import Dict, Any, Optional, List

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
USER_XLSX = "SISTEMA NOVO_ITAMINAS.xlsx" 
USER_SHEET_NAME = "Resultado da consulta" 

# Timeouts (conexão, leitura)
CONNECT_TIMEOUT = 10
READ_TIMEOUT = 60

logging.basicConfig(level=logging.INFO, format="%(asctime)s | %(levelname)s | %(message)s")
log = logging.getLogger("realiza.setup")

# ==============================================================================
# Helpers
# ==============================================================================

def mask_email(email: str) -> str:
    if not email or "@" not in email:
        return str(email)
    name, dom = email.split("@", 1)
    return (name[:2] + "***@" + dom) if len(name) > 2 else ("***@" + dom)

def find_col(df: pd.DataFrame, candidates: List[str]) -> str:
    """Encontra coluna por nome exato (case-insensitive) ou prefixo."""
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

# A função fetch_profile_id foi removida para evitar o erro HTTP 500

# ==============================================================================
# Leitura Excel
# ==============================================================================

def load_excel_sheet(path: str, sheet_name: str) -> pd.DataFrame:
    base = os.path.dirname(os.path.realpath(__file__))
    full = os.path.join(base, path)
    try:
        df = pd.read_excel(full, sheet_name=sheet_name, engine="openpyxl", dtype=str, keep_default_na=False)
        log.info(f"✔ Aba '{sheet_name}' do Excel '{path}' carregada com sucesso.")
        return df
    except Exception as e:
        log.warning(f"✖ Aba '{sheet_name}' não encontrada ou erro ao ler: {e}. Retornando DataFrame vazio.")
        return pd.DataFrame()

# ==============================================================================
# Users (gestores) - SIMPLIFICADO E FINAL
# ==============================================================================

def create_users_simplified(session: requests.Session, token: str) -> None:
    # Assumindo que a aba 'Resultado da consulta' do SISTEMA NOVO_ITAMINAS.xlsx tem os gestores
    df = load_excel_sheet(USER_XLSX, USER_SHEET_NAME)
    if df.empty:
        log.info("✖ DataFrame de Usuários está vazio. Pulando criação de usuários.")
        return
    
    try:
        email_col = find_col(df, ["E-mail Gestor", "Email Gestor", "Gestor E-mail"])
        full_col = find_col(df, ["Gestor", "Nome Gestor"])
    except KeyError:
        log.warning("✖ Colunas necessárias para Gestores não encontradas. Pulando criação de usuários.")
        return

    # Deduplicar a lista de gestores únicos
    df_unique = df.drop_duplicates(subset=[email_col])
    log.info(f"Processando {len(df_unique)} gestores únicos para criação de usuários.")

    # O profile_id foi removido para evitar o erro HTTP 500
    profile_id = None 

    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    path_opts = ["/api/user/manager/new-user", "/user/manager/new-user"]

    created = 0
    already = 0
    fail = 0

    for idx, row in df_unique.iterrows():
        full_name = nonempty(row.get(full_col))
        email = nonempty(row.get(email_col))

        if not full_name or not email:
            log.info(f"↷ Linha {idx+2} ignorada: nome ou e-mail do gestor vazio.")
            continue
        
        first, *rest = full_name.split()
        surname = " ".join(rest)

        # ATENÇÃO: Os campos 'branch' e 'profileId' foram removidos para simplificar a criação e evitar erros
        payload = {
            "name": full_name,
            "role": "ROLE_CLIENT_MANAGER",
            "firstName": first,
            "surname": surname,
            "email": email.strip().lower(),
            "enterprise": "CLIENT",
            "idEnterprise": CLIENT_GLOBAL_ID,
        }

        ok_this = False
        for p in path_opts:
            url = f"{APP_URL}{p}"
            try:
                r = request_with_retry(session, "POST", url, headers=headers, json=payload)
                
                if r.status_code in (200, 201):
                    log.info(f"✔ Usuário criado: {email}")
                    created += 1
                    ok_this = True
                    break
                
                if r.status_code == 409 or "Duplicate entry" in (r.text or ""):
                    log.info(f"↷ Usuário já existia: {email}")
                    already += 1
                    ok_this = True
                    break
            except Exception as e:
                last = str(e)
                continue
        
        if not ok_this and 'r' in locals() and r.status_code not in (409, 200, 201):
            log.error(f"✖ Falha ao criar usuário '{email}' | status={r.status_code} | resp={r.text[:100]}")
            fail += 1
        elif not ok_this:
            log.error(f"✖ Falha ao criar usuário '{email}' | erro={last if 'last' in locals() else 'desconhecido'}")
            fail += 1

    print(f"\nResumo Usuários → criados: {created} | existentes: {already} | falhas: {fail}")

# ==============================================================================
# Main
# ==============================================================================

def main():
    token = login()
    
    with requests.Session() as session:
        # A criação de filiais foi removida
        create_users_simplified(session, token)

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("Execução interrompida pelo usuário.")