#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import pandas as pd
import requests
import mysql.connector
import unicodedata
import json
import uuid

# ==============================================================================
# CONFIGURAÇÕES GERAIS
# ==============================================================================
#APP_URL = "https://realiza-api-development.onrender.com".rstrip("/")
APP_URL = "https://realiza.onrender.com".rstrip("/")

BASE_DOCS_FOLDER = "IS-508"
MAP_FILE_NAME = "Mapa.xlsx"
MAP_SHEET_NAME = "DOCS"

USER_LOGIN = {"email": "realiza@assessoria.com", "password": "senha123"}
DB_CONFIG = {
    "host": "177.170.30.9", "port": 8004, "user": "veridion_user",
    "password": "SenhaSegura123!", "database": "dbrealiza"
}
REQ_TIMEOUT = 60


# ==============================================================================
# HELPERS
# ==============================================================================
def login() -> str:
    # ... (código original sem alteração)
    url = f"{APP_URL}/login"
    print("Realizando login...")
    r = requests.post(url, json=USER_LOGIN, timeout=REQ_TIMEOUT)
    r.raise_for_status()
    data = r.json()
    token = data.get("token") or data.get("access_token")
    if not token: raise RuntimeError("Login OK, mas não veio token.")
    print("✔ Login realizado com sucesso!")
    return token


def print_summary(title: str, ok: int, created: int, fail: int, skipped: int, matrix_created: int):
    print(
        f"\n{title} → Uploads: {ok} | Registros Doc Criados: {created} | Novas Matrizes: {matrix_created} | Falhas: {fail} | Ignorados: {skipped}")


def remove_accents(text: str) -> str:
    # ... (código original sem alteração)
    if not text: return ""
    return "".join(c for c in unicodedata.normalize('NFD', str(text)) if unicodedata.category(c) != 'Mn')


# ==============================================================================
# FUNÇÕES DE PREPARAÇÃO
# ==============================================================================
def fetch_all_mappings_from_db(cursor) -> dict:
    # ... (código original sem alteração, apenas corrigindo a query de subcontractor)
    print("\nCarregando mapas de tradução do banco de dados...")
    maps = {}
    cursor.execute(
        "SELECT eb.cpf, e.id_employee FROM employee e JOIN employee_brazilian eb ON e.id_employee = eb.id_employee WHERE eb.cpf IS NOT NULL")
    maps['employees'] = {row[0]: row[1] for row in cursor.fetchall()}
    print(f"✔ {len(maps['employees'])} funcionários carregados.")
    cursor.execute(
        "SELECT p.cnpj, p.id_provider FROM provider p JOIN provider_supplier ps ON p.id_provider = ps.id_provider WHERE p.cnpj IS NOT NULL")
    maps['suppliers'] = {row[0]: row[1] for row in cursor.fetchall()}
    print(f"✔ {len(maps['suppliers'])} fornecedores carregados.")
    cursor.execute(
        "SELECT p.cnpj, p.id_provider FROM provider p JOIN provider_subcontractor psc ON p.id_provider = psc.id_provider WHERE p.cnpj IS NOT NULL")
    maps['subcontractors'] = {row[0]: row[1] for row in cursor.fetchall()}
    print(f"✔ {len(maps['subcontractors'])} subcontratados carregados.")
    cursor.execute("SELECT name, id_document, type FROM document_matrix")
    maps['doc_matrix'] = {remove_accents(name).lower(): (id_doc, type_doc) for name, id_doc, type_doc in
                          cursor.fetchall()}
    print(f"✔ {len(maps['doc_matrix'])} tipos de documentos (matriz) carregados.")
    cursor.execute(
        "SELECT de.id_employee, d.id_document, de.id_documentation FROM document_employee de JOIN document d ON de.id_documentation = d.id_documentation")
    maps['doc_employee_links'] = {(emp_id, doc_id): doc_emp_id for emp_id, doc_id, doc_emp_id in cursor.fetchall()}
    print(f"✔ {len(maps['doc_employee_links'])} links de documentos de funcionários carregados.")
    cursor.execute(
        "SELECT dps.id_provider_supplier, d.id_document, dps.id_documentation FROM document_provider_supplier dps JOIN document d ON dps.id_documentation = d.id_documentation")
    maps['doc_supplier_links'] = {(sup_id, doc_id): doc_sup_id for sup_id, doc_id, doc_sup_id in cursor.fetchall()}
    print(f"✔ {len(maps['doc_supplier_links'])} links de documentos de fornecedores carregados.")
    cursor.execute(
        "SELECT dpsc.id_provider_supplier, d.id_document, dpsc.id_documentation FROM document_provider_subcontractor dpsc JOIN document d ON dpsc.id_documentation = d.id_documentation")
    maps['doc_subcontractor_links'] = {(sub_id, doc_id): doc_sub_id for sub_id, doc_id, doc_sub_id in cursor.fetchall()}
    print(f"✔ {len(maps['doc_subcontractor_links'])} links de documentos de subcontratados carregados.")
    return maps


# ALTERADO: A função agora usa 'documentMatrixId' no payload
def create_document_record(session, subject_type, subject_id, doc_matrix_id, doc_matrix_type, doc_name_excel):
    create_url = f"{APP_URL}/document/{subject_type}"
    dto_payload = {
        "title": doc_name_excel,
        "type": doc_matrix_type,
        "documentMatrixId": doc_matrix_id,  # Nome do campo corrigido
        subject_type: subject_id
    }
    part_name_map = {
        'employee': 'documentEmployeeRequestDto',
        'supplier': 'documentSupplierRequestDto',
        'subcontractor': 'documentSubcontractorRequestDto'
    }
    part_name = part_name_map.get(subject_type)
    if not part_name: raise ValueError(f"Tipo de sujeito desconhecido: {subject_type}")

    multipart_payload = {part_name: (None, json.dumps(dto_payload), 'application/json')}
    r = session.post(create_url, files=multipart_payload)
    r.raise_for_status()
    response_data = r.json()
    new_doc_id = response_data.get('idDocument')
    if not new_doc_id: raise ValueError(
        f"API criou o registro mas não retornou 'idDocument'. Resposta: {response_data}")
    return new_doc_id


# ==============================================================================
# LÓGICA PRINCIPAL - UPLOAD DE DOCUMENTOS
# ==============================================================================
def upload_documents(token: str):
    # Removido script_dir e a lógica original, usando o caminho fixo:
    base_folder_path = r"C:\Users\User\Downloads\IS-508"
    
    # Esta linha agora usa o novo caminho base
    map_file_path = os.path.join(base_folder_path, MAP_FILE_NAME)

    ok, created, fail, skipped, matrix_created = 0, 0, 0, 0, 0
    conn = None
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        maps = fetch_all_mappings_from_db(cursor)

        print(f"\nLendo arquivo de mapa: {map_file_path}")
        df = pd.read_excel(map_file_path, sheet_name=MAP_SHEET_NAME, dtype=str, keep_default_na=False)
        print(f"Iniciando processamento de {len(df)} linhas do arquivo de mapa...")

        with requests.Session() as s:
            s.headers.update({"Authorization": f"Bearer {token}"})

            col_doc_name = 'nomeDocumento'
            col_path = 'caminho'
            col_cnpj = 'cnpj'
            col_cpf = 'cpf'

            for idx, row in df.iterrows():
                doc_name_excel = row.get(col_doc_name, '').strip()
                path_excel = row.get(col_path, '').strip()
                cnpj = row.get(col_cnpj, '').strip()
                cpf = row.get(col_cpf, '').strip()

                if not doc_name_excel or not path_excel:
                    skipped += 1
                    continue

                file_path = os.path.join(base_folder_path, path_excel.replace('\\', os.sep))
                if not os.path.exists(file_path):
                    print(f"↷ Linha {idx + 2}: Arquivo '{file_path}' não encontrado. Ignorando.")
                    skipped += 1
                    continue

                subject_id, subject_type = (None, None)
                # Lógica para encontrar o sujeito (funcionário ou empresa)
                # ... (código original sem alteração)
                if cpf and cpf in maps['employees']:
                    subject_id, subject_type = (maps['employees'][cpf], 'employee')
                elif cnpj:
                    if cnpj in maps['suppliers']:
                        subject_id, subject_type = (maps['suppliers'][cnpj], 'supplier')
                    elif cnpj in maps['subcontractors']:
                        subject_id, subject_type = (maps['subcontractors'][cnpj], 'subcontractor')

                if not subject_id:
                    print(f"↷ Linha {idx + 2}: Sujeito (CPF/CNPJ) não encontrado. Ignorando.")
                    skipped += 1
                    continue

                # --- LÓGICA DE CRIAÇÃO DE DOCUMENT_MATRIX ALTERADA ---
                doc_matrix_data = maps['doc_matrix'].get(remove_accents(doc_name_excel).lower())

                if not doc_matrix_data:
                    print(f"INFO: Documento '{doc_name_excel}' não encontrado na matriz. Criando no banco...")
                    try:
                        new_matrix_id = str(uuid.uuid4())
                        default_doc_type = 'geral'  # Valor padrão, ajuste se necessário
                        # is_document_unique é definido como false (0) por padrão
                        cursor.execute(
                            "INSERT INTO document_matrix (id_document, name, type, is_document_unique) VALUES (%s, %s, %s, 0)",
                            (new_matrix_id, doc_name_excel, default_doc_type)
                        )
                        conn.commit()
                        print(f"✔ Matriz de Documento '{doc_name_excel}' criada com sucesso.")

                        doc_matrix_id = new_matrix_id
                        doc_matrix_type = default_doc_type
                        maps['doc_matrix'][remove_accents(doc_name_excel).lower()] = (doc_matrix_id, doc_matrix_type)
                        matrix_created += 1
                    except mysql.connector.Error as err:
                        print(f"✖ Falha ao criar matriz de doc para '{doc_name_excel}': {err}")
                        fail += 1
                        continue
                else:
                    doc_matrix_id, doc_matrix_type = doc_matrix_data
                # --- FIM DA ALTERAÇÃO ---

                id_documentation = None
                link_map_name = f"doc_{subject_type}_links"
                link_key = (subject_id, doc_matrix_id)
                id_documentation = maps[link_map_name].get(link_key)

                if not id_documentation:
                    try:
                        print(f"INFO: Link de doc para '{doc_name_excel}' não existe. Criando via API...")
                        new_id = create_document_record(s, subject_type, subject_id, doc_matrix_id, doc_matrix_type,
                                                        doc_name_excel)
                        print(f"✔ Registro de documento criado com ID: {new_id[:8]}")
                        id_documentation = new_id
                        maps[link_map_name][link_key] = new_id
                        created += 1
                    except Exception as e:
                        print(f"✖ Falha ao criar registro de doc para '{doc_name_excel}': {e}")
                        fail += 1
                        continue

                url = f"{APP_URL}/document/{subject_type}/{id_documentation}/upload"
                try:
                    with open(file_path, 'rb') as f:
                        files = {'file': (os.path.basename(file_path), f)}
                        r = s.post(url, files=files, timeout=REQ_TIMEOUT)

                    if r.status_code in [200, 201]:
                        print(f"✔ Upload do doc '{doc_name_excel}' para sujeito {subject_id[:8]}... concluído.")
                        ok += 1
                    else:
                        print(
                            f"✖ Falha no upload para doc ID {id_documentation[:8]} (Status: {r.status_code}): {r.text[:200]}")
                        fail += 1
                except Exception as e:
                    print(f"✖ Exceção durante o upload para doc ID {id_documentation[:8]}: {e}")
                    fail += 1

    except Exception as e:
        print(f"✖ ERRO FATAL: {e}")
    finally:
        if conn and conn.is_connected():
            conn.close()
            print("\nConexão com o banco de dados encerrada.")

    print_summary("Resumo dos Uploads", ok, created, fail, skipped, matrix_created)


# ==============================================================================
# EXECUÇÃO PRINCIPAL
# ==============================================================================
if __name__ == "__main__":
    try:
        api_token = login()
        upload_documents(api_token)
    except Exception as e:
        print(f"\nOcorreu um erro inesperado na execução do script: {e}")