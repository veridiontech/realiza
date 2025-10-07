#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import requests
import mysql.connector

# ==============================================================================
# CONFIGURAÇÕES GERAIS
# ==============================================================================
APP_URL = "http://localhost:8080".rstrip("/")
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
    """Realiza o login na API e retorna o token de autenticação."""
    url = f"{APP_URL}/login"
    print("Realizando login...")
    r = requests.post(url, json=USER_LOGIN, timeout=REQ_TIMEOUT)
    r.raise_for_status()
    data = r.json()
    token = data.get("token") or data.get("access_token")
    if not token:
        raise RuntimeError(f"Login OK, mas não veio token. Resposta: {data}")
    print("✔ Login realizado com sucesso!")
    return token


def print_summary(title: str, ok: int, fail: int):
    """Exibe um resumo formatado da operação."""
    print(f"\n{title} → Aprovadas: {ok} | Falhas: {fail}")


# ==============================================================================
# LÓGICA PRINCIPAL
# ==============================================================================
def approve_pending_solicitations(token: str):
    """
    Busca todas as solicitações de contrato pendentes no banco de dados
    e as aprova via API.
    """
    print("\nIniciando processo de aprovação de solicitações de contrato...")

    # --- 1. BUSCAR IDs DAS SOLICITAÇÕES PENDENTES NO BANCO ---
    solicitation_ids = []
    conn = None
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("✔ Conectado ao banco de dados para buscar solicitações pendentes.")

        # Busca IDs de solicitações de contrato ('0') com status 'PENDING'
        # ATENÇÃO: Confirme se 'item_management' é o nome correto da sua tabela de solicitações.
        query = """
            SELECT id_solicitation 
            FROM item_management 
            WHERE status = 'PENDING' AND solicitation_type = '0'
        """
        cursor.execute(query)

        results = cursor.fetchall()
        solicitation_ids = [item[0] for item in results]

        if not solicitation_ids:
            print("✔ Nenhuma solicitação de contrato pendente encontrada. Nada a fazer.")
            return

        print(f"✔ {len(solicitation_ids)} solicitações de contrato pendentes encontradas.")

    except mysql.connector.Error as err:
        print(f"✖ ERRO FATAL de banco de dados: {err}")
        return
    finally:
        if conn and conn.is_connected():
            conn.close()
            print("Conexão com o banco de dados encerrada.")

    # --- 2. FAZER AS CHAMADAS À API PARA APROVAR ---
    ok, fail = 0, 0
    with requests.Session() as s:
        s.headers.update({"Authorization": f"Bearer {token}"})

        print("\nIniciando chamadas à API para aprovar as solicitações...")

        for solicitation_id in solicitation_ids:
            url = f"{APP_URL}/item-management/{solicitation_id}/approve"

            try:
                # O método para aprovar é PATCH e não precisa de corpo (body)
                r = s.patch(url, timeout=REQ_TIMEOUT)

                if r.status_code == 200:
                    print(f"✔ Solicitação {solicitation_id[:8]}... aprovada com sucesso.")
                    ok += 1
                else:
                    print(
                        f"✖ Falha ao aprovar solicitação {solicitation_id[:8]}... (Status: {r.status_code}): {r.text[:200]}")
                    fail += 1
            except requests.RequestException as e:
                print(f"✖ Exceção de rede na solicitação {solicitation_id[:8]}...: {e}")
                fail += 1

    print_summary("Resumo da Aprovação de Solicitações", ok, fail)


# ==============================================================================
# EXECUÇÃO PRINCIPAL
# ==============================================================================
if __name__ == "__main__":
    try:
        api_token = login()
        approve_pending_solicitations(api_token)
    except Exception as e:
        print(f"\nOcorreu um erro inesperado na execução do script: {e}")