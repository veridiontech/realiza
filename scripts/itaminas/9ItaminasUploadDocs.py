#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import pandas as pd
import requests
import mysql.connector
import unicodedata

# ==============================================================================
# CONFIGURAÇÕES GERAIS
# ==============================================================================
APP_URL = "http://localhost:8080".rstrip("/")
BASE_DOCS_FOLDER = "IS-508"  # Pasta irmã onde estão os documentos e o mapa
MAP_FILE_NAME = "Mapa.xlsx"
MAP_SHEET_NAME = "Docs"  # ATENÇÃO: Altere se o nome da aba no seu mapa for diferente

USER_LOGIN = {"email": "realiza@assessoria.com", "password": "senha123"}
DB_CONFIG = {
    "host": "177.170.30.9", "port": 8004, "user": "veridion_user",
    "password": "SenhaSegura123!", "database": "dbrealiza"
}
REQ_TIMEOUT = 60  # Timeout maior para uploads


# ==============================================================================
# HELPERS
# ==============================================================================
def login() -> str:
    # ... (código original)
    url = f"{APP_URL}/login"
    print("Realizando login...")
    r = requests.post(url, json=USER_LOGIN, timeout=REQ_TIMEOUT)
    r.raise_for_status()
    data = r.json()
    token = data.get("token") or data.get("access_token")
    if not token: raise RuntimeError("Login OK, mas não veio token.")
    print("✔ Login realizado com sucesso!")
    return token


def print_summary(title: str, ok: int, fail: int, skipped: int):
    print(f"\n{title} → Uploads com sucesso: {ok} | Falhas: {fail} | Ignorados: {skipped}")


def remove_accents(text: str) -> str:
    if not text: return ""
    return "".join(c for c in unicodedata.normalize('NFD', str(text)) if unicodedata.category(c) != 'Mn')


# ==============================================================================
# FUNÇÕES DE PREPARAÇÃO
# ==============================================================================
def fetch_all_mappings_from_db(cursor) -> dict:
    """Carrega todos os mapas de tradução do banco de dados para a memória."""
    print("\nCarregando mapas de tradução do banco de dados...")
    maps = {}

    # Funcionários (CPF -> ID)
    cursor.execute(
        "SELECT eb.cpf, e.id_employee FROM employee e JOIN employee_brazilian eb ON e.id_employee = eb.id_employee WHERE eb.cpf IS NOT NULL")
    maps['employees'] = {row[0]: row[1] for row in cursor.fetchall()}
    print(f"✔ {len(maps['employees'])} funcionários carregados.")

    # Fornecedores (CNPJ -> ID)
    cursor.execute(
        "SELECT p.cnpj, p.id_provider FROM provider p JOIN provider_supplier ps ON p.id_provider = ps.id_provider WHERE p.cnpj IS NOT NULL")
    maps['suppliers'] = {row[0]: row[1] for row in cursor.fetchall()}
    print(f"✔ {len(maps['suppliers'])} fornecedores carregados.")

    # Subcontratados (CNPJ -> ID) - pode haver sobreposição, mas não há problema
    cursor.execute(
        "SELECT p.cnpj, p.id_provider FROM provider p JOIN provider_subcontractor psc ON p.id_provider = psc.id_provider WHERE p.cnpj IS NOT NULL")
    maps['subcontractors'] = {row[0]: row[1] for row in cursor.fetchall()}
    print(f"✔ {len(maps['subcontractors'])} subcontratados carregados.")

    # Matriz de Documentos (Nome Normalizado -> ID)
    cursor.execute("SELECT name, id_document FROM document_matrix")
    maps['doc_matrix'] = {remove_accents(name).lower(): id_doc for name, id_doc in cursor.fetchall()}
    print(f"✔ {len(maps['doc_matrix'])} tipos de documentos (matriz) carregados.")

    # Links Documento-Funcionário ((employee_id, doc_matrix_id) -> id_documentation)
    cursor.execute("SELECT id_employee, id_document, id_documentation FROM document_employee")
    maps['doc_employee_links'] = {(emp_id, doc_id): doc_emp_id for emp_id, doc_id, doc_emp_id in cursor.fetchall()}
    print(f"✔ {len(maps['doc_employee_links'])} links de documentos de funcionários carregados.")

    # Links Documento-Fornecedor ((supplier_id, doc_matrix_id) -> id_documentation)
    cursor.execute("SELECT id_provider_supplier, id_document, id_documentation FROM document_provider_supplier")
    maps['doc_supplier_links'] = {(sup_id, doc_id): doc_sup_id for sup_id, doc_id, doc_sup_id in cursor.fetchall()}
    print(f"✔ {len(maps['doc_supplier_links'])} links de documentos de fornecedores carregados.")

    return maps


def build_file_cache(base_folder: str) -> dict:
    """Varre a pasta base de documentos e cria um mapa de nome de arquivo para caminho completo."""
    print(f"\nCriando cache de arquivos na pasta '{base_folder}'...")
    file_cache = {}
    for root, _, files in os.walk(base_folder):
        for file in files:
            # Chave é o nome do arquivo sem extensão, para bater com a coluna 'arquivo' do mapa
            filename_no_ext = os.path.splitext(file)[0]
            file_cache[filename_no_ext] = os.path.join(root, file)
    print(f"✔ Cache de arquivos criado. {len(file_cache)} arquivos encontrados.")
    return file_cache


# ==============================================================================
# LÓGICA PRINCIPAL - UPLOAD DE DOCUMENTOS
# ==============================================================================
def upload_documents(token: str):
    script_dir = os.path.dirname(__file__)
    base_folder_path = os.path.join(script_dir, BASE_DOCS_FOLDER)
    map_file_path = os.path.join(base_folder_path, MAP_FILE_NAME)

    conn = None
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()

        # 1. Preparação
        maps = fetch_all_mappings_from_db(cursor)
        file_cache = build_file_cache(base_folder_path)
        df = pd.read_excel(map_file_path, sheet_name=MAP_SHEET_NAME, dtype=str, keep_default_na=False)
        print(f"\nIniciando processamento de {len(df)} linhas do arquivo de mapa...")

        ok, fail, skipped = 0, 0, 0

        # 2. Loop de processamento
        with requests.Session() as s:
            s.headers.update({"Authorization": f"Bearer {token}"})

            for idx, row in df.iterrows():
                # Extrai dados do Excel
                doc_name_excel = row.get('nomeDocumento', '').strip()
                file_name_excel = row.get('arquivo', '').strip()  # Nome do PDF sem extensão
                cnpj = row.get('cnpj', '').strip()
                cpf = row.get('cpf', '').strip()

                if not doc_name_excel or not file_name_excel:
                    print(f"↷ Linha {idx + 2} ignorada: Nome do documento ou nome do arquivo vazio.")
                    skipped += 1
                    continue

                subject_id = None
                subject_type = None

                # Determina o sujeito (Empresa ou Funcionário)
                if cpf and cpf in maps['employees']:
                    subject_id = maps['employees'][cpf]
                    subject_type = 'employee'
                elif cnpj:
                    if cnpj in maps['suppliers']:
                        subject_id = maps['suppliers'][cnpj]
                        subject_type = 'supplier'
                    elif cnpj in maps['subcontractors']:
                        subject_id = maps['subcontractors'][cnpj]
                        subject_type = 'subcontractor'

                if not subject_id:
                    print(f"↷ Linha {idx + 2} ignorada: Sujeito (CPF {cpf} ou CNPJ {cnpj}) não encontrado no banco.")
                    skipped += 1
                    continue

                # Encontra o ID da matriz do documento
                doc_matrix_id = maps['doc_matrix'].get(remove_accents(doc_name_excel).lower())
                if not doc_matrix_id:
                    print(f"↷ Linha {idx + 2} ignorada: Tipo de documento '{doc_name_excel}' não encontrado na matriz.")
                    skipped += 1
                    continue

                # Encontra o ID da documentação final (o alvo do upload)
                id_documentation = None
                if subject_type == 'employee':
                    id_documentation = maps['doc_employee_links'].get((subject_id, doc_matrix_id))
                elif subject_type == 'supplier':
                    id_documentation = maps['doc_supplier_links'].get((subject_id, doc_matrix_id))
                # Adicionar lógica para subcontractor se necessário

                if not id_documentation:
                    print(
                        f"↷ Linha {idx + 2} ignorada: Link de documentação para '{doc_name_excel}' não encontrado para o sujeito {subject_id[:8]}.")
                    skipped += 1
                    continue

                # Encontra o arquivo físico
                file_path = file_cache.get(file_name_excel)
                if not file_path:
                    print(
                        f"↷ Linha {idx + 2} ignorada: Arquivo físico '{file_name_excel}.pdf' não encontrado no disco.")
                    skipped += 1
                    continue

                # Monta a URL e faz o upload
                endpoint = f"/document/{subject_type}/{id_documentation}/upload"
                url = f"{APP_URL}{endpoint}"

                try:
                    with open(file_path, 'rb') as f:
                        files = {'file': (os.path.basename(file_path), f)}
                        r = s.post(url, files=files, timeout=REQ_TIMEOUT)

                    if r.status_code in [200, 201]:
                        print(f"✔ Sucesso no upload do documento '{doc_name_excel}' para o sujeito {subject_id[:8]}.")
                        ok += 1
                    else:
                        print(
                            f"✖ Falha no upload para doc ID {id_documentation[:8]} (Status: {r.status_code}): {r.text[:200]}")
                        fail += 1
                except Exception as e:
                    print(f"✖ Exceção durante o upload para doc ID {id_documentation[:8]}: {e}")
                    fail += 1

    except FileNotFoundError:
        print(f"✖ ERRO FATAL: Arquivo de mapa '{map_file_path}' não encontrado.")
    except Exception as e:
        print(f"✖ ERRO FATAL: {e}")
    finally:
        if conn and conn.is_connected():
            conn.close()
            print("\nConexão com o banco de dados encerrada.")

    print_summary("Resumo dos Uploads", ok, fail, skipped)


# ==============================================================================
# EXECUÇÃO PRINCIPAL
# ==============================================================================
if __name__ == "__main__":
    try:
        api_token = login()
        upload_documents(api_token)
    except Exception as e:
        print(f"\nOcorreu um erro inesperado na execução do script: {e}")