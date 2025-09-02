#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import time
import hashlib
import pandas as pd
import requests

APP_URL = "http://localhost:8080".rstrip("/")

USER_LOGIN = {
    "email": "realiza@assessoria.com",
    "password": "senha123",
}

# IDs globais
profile_id = "b4be52d6-e9db-44f0-85a6-22d1dfae5ba3"
client_global_id = "b4be52d6-e9db-44f0-85a6-22d1dfae5ba3"

# Mapa de filiais criadas: { nome: idBranch }
branches = {}

# Excel
EXCEL_FILE = "ITAMINAS_CONFIGURACOES.xlsx"
SHEET_BRANCHES = "UNIDADES"
SHEET_USERS = "USUARIOS"

# Timeout e retry
REQ_TIMEOUT = 20
RETRY_SLEEP_BASE = 1.2
MAX_RETRIES = 2


# ========== LOG HELPERS ==========
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

def check_empty(value):
    return value if (value is not None and f"{value}".strip() != "") else None

def split_fullname(fullname):
    parts = (fullname or "").strip().split()
    first = parts[0] if parts else None
    last = " ".join(parts[1:]) if len(parts) > 1 else None
    return first, last


# ========== REQUEST (RETRY) ==========
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


# ========== AUTH ==========
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


# ========== EXCEL ==========
def import_data(file_name):
    dir_atual = os.path.dirname(os.path.realpath(__file__))
    path = os.path.join(dir_atual, file_name)
    try:
        dados = pd.read_excel(
            path,
            sheet_name=None,
            engine="openpyxl",
            dtype=str,
            keep_default_na=False,  # células vazias viram ''
        )
        print("Dados importados com sucesso!")
        return dados
    except Exception as e:
        print("Erro ao importar o Excel:", e)
        return None


# ========== PROFILE ==========
def resolve_profile_id(session: requests.Session, desired_name: str = "Padrão") -> str:
    """
    Mantém a funcionalidade original:
    GET /profile/by-name/{client_global_id} -> pega o profile com nome 'Padrão'
    """
    url = f"{APP_URL}/profile/by-name/{client_global_id}"
    r = request_with_retry(session, "GET", url)
    if r.status_code != 200:
        print(f"⚠ falha ao buscar perfis ({r.status_code}) | resp={r.text[:200]}")
        return None
    profiles = r.json() or []
    pid = next((p.get("id") for p in profiles if p.get("profileName") == desired_name), None)
    if pid:
        print(f"Profile ID encontrado: {pid}")
    else:
        print("⚠ profile 'Padrão' não encontrado no retorno.")
    return pid


# ========== BRANCHES ==========
def create_branches(session: requests.Session, data):
    """
    Lê a aba UNIDADES e cria filiais em /branch
    Log: ✔ criado | ↷ 409 existente | ✖ falha (+ resumo)
    Mantém o comportamento original de preencher `branches[name] = idBranch` ao criar com sucesso.
    """
    global branches
    url = f"{APP_URL}/branch"

    units = data.get(SHEET_BRANCHES, None)
    if units is None or units.empty:
        print("Dados de unidades não encontrados ou vazios.")
        return

    b_ok = b_already = b_fail = b_skipped = 0

    for _, row in units.iterrows():
        # pula linha totalmente vazia
        if not any(str(v).strip() for v in row.values):
            b_skipped += 1
            continue

        name = check_empty(row.get("Unidade*"))
        if not name:
            b_skipped += 1
            continue

        payload = {
            "name": name,
            "cnpj": check_empty(row.get("CNPJ*")),
            "address": check_empty(row.get("Endereço*")),
            "number": check_empty(row.get("Número*")),
            "cep": check_empty(row.get("CEP*")),
            "city": check_empty(row.get("Cidade*")),
            "state": check_empty(row.get("Estado*")),
            "telephone": check_empty(row.get("Telefone - 1")),
            "client": client_global_id,
            "replicateFromBase": True,
        }

        started = time.time()
        chk = checksum(payload)
        try:
            r = request_with_retry(session, "POST", url, json=payload)
            ms = round((time.time() - started) * 1000)
            if r.status_code in (200, 201):
                data_json = r.json() or {}
                id_branch = data_json.get("idBranch") or data_json.get("id")
                branches[name] = id_branch
                print(f"✔ filial criada: '{name}' -> id={id_branch} | chk={chk} | {ms}ms")
                b_ok += 1
            elif r.status_code == 409:
                print(f"↷ filial já existia (409): '{name}' | chk={chk}")
                # Sem endpoint de busca por nome → não é possível recuperar id aqui.
                b_already += 1
            else:
                print(f"✖ falha criar filial ({r.status_code}): '{name}' | resp={r.text[:200]} | chk={chk}")
                b_fail += 1
        except Exception as e:
            print(f"✖ exceção ao criar filial '{name}': {e} | chk={chk}")
            b_fail += 1

    print_summary("Filiais", b_ok, b_already, b_fail, b_skipped)


# ========== USERS ==========
def create_users(session: requests.Session, data):
    """
    Lê a aba USUARIOS e cria usuários em /user/manager/new-user
    Mantém a busca do profile “Padrão”.
    """
    url = f"{APP_URL}/user/manager/new-user"

    # resolve profile “Padrão”
    pid = resolve_profile_id(session, desired_name="Padrão")
    if not pid:
        print("✖ não foi possível resolver o profile 'Padrão'; abortando criação de usuários.")
        return

    users_df = data.get(SHEET_USERS, None)
    if users_df is None or users_df.empty:
        print("Dados de usuários não encontrados ou vazios.")
        return

    u_ok = u_already = u_fail = u_skipped = 0

    for i, row in users_df.iterrows():
        # Pula linha totalmente vazia
        if not any(str(v).strip() for v in row.values):
            u_skipped += 1
            continue

        full_name = check_empty(row.get("Funcionário*"))
        cpf = check_empty(row.get("CPF*"))
        email = (check_empty(row.get("E-mail*")) or "").lower().strip()
        branch_name = check_empty(row.get("Unidade*"))

        if not full_name or not email or not branch_name:
            u_skipped += 1
            print(f"↷ linha {i} ignorada: campos obrigatórios ausentes (Funcionário*/E-mail*/Unidade*).")
            continue

        # pega id da filial criada (se não existir, não consigo criar o user)
        branch_id = branches.get(branch_name)
        if not branch_id:
            u_fail += 1
            print(f"✖ linha {i}: filial '{branch_name}' sem id (talvez já existisse e não recuperei o id).")
            continue

        first, last = split_fullname(full_name)
        payload = {
            "cpf": cpf,
            "role": "ROLE_CLIENT_MANAGER",
            "firstName": first,
            "surname": last,
            "email": email,
            "branch": branch_id,
            "enterprise": "CLIENT",
            "idEnterprise": client_global_id,
            "profileId": pid,
        }
        chk = checksum(payload)
        started = time.time()

        try:
            r = request_with_retry(session, "POST", url, json=payload)
            ms = round((time.time() - started) * 1000)

            if r.status_code in (200, 201):
                # algumas APIs retornam o user id; se tiver, loga
                uid = (r.json() or {}).get("id") if r.headers.get("Content-Type", "").startswith("application/json") else "—"
                print(f"✔ usuário criado: {full_name} <{mask_email(email)}> -> id={uid} | chk={chk} | {ms}ms")
                u_ok += 1
            elif r.status_code == 409:
                print(f"↷ usuário já existia (409): {full_name} <{mask_email(email)}> | chk={chk}")
                u_already += 1
            else:
                print(f"✖ falha criar usuário ({r.status_code}): {full_name} <{mask_email(email)}> | resp={r.text[:200]} | chk={chk}")
                u_fail += 1

        except Exception as e:
            print(f"✖ exceção linha {i}: {e} | user={full_name} <{mask_email(email)}> | chk={chk}")
            u_fail += 1

    print_summary("Usuários", u_ok, u_already, u_fail, u_skipped)


# ========== MAIN ==========
def main():
    # login
    token = login()

    # importar Excel
    data = import_data(EXCEL_FILE)
    if data is None:
        return

    # sessão com headers
    with requests.Session() as s:
        s.headers.update({
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json",
        })

        # 1) criar filiais
        create_branches(s, data)

        # 2) criar usuários
        create_users(s, data)


if __name__ == "__main__":
    main()
