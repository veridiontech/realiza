#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import pandas as pd
import requests
import mysql.connector
import unicodedata
import json
import uuid
import time # Adicionado para usar o time.sleep no login

# ==============================================================================
# CONFIGURAÇÕES GERAIS (Mantenha as mesmas do Script 08)
# ==============================================================================
APP_URL = "https://realiza.onrender.com".rstrip("/" )

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
    url = f"{APP_URL}/login"
    print("Realizando login...")
    r = requests.post(url, json=USER_LOGIN, timeout=REQ_TIMEOUT)
    r.raise_for_status()
    data = r.json()
    token = data.get("token") or data.get("access_token")
    if not token: raise RuntimeError("Login OK, mas não veio token.")
    print("✔ Login realizado com sucesso!")
    return token


def print_summary(title: str, created: int, fail: int, skipped: int, matrix_created: int):
    print(
        f"\n{title} → Registros Doc Criados: {created} | Novas Matrizes: {matrix_created} | Falhas: {fail} | Ignorados: {skipped}")


def remove_accents(text: str) -> str:
    if not text: return ""
    return "".join(c for c in unicodedata.normalize('NFD', str(text)) if unicodedata.category(c) != 'Mn')


# ==============================================================================
# FUNÇÕES DE PREPARAÇÃO (Corrigidas para buscar ID do Contrato)
# ==============================================================================
def fetch_all_mappings_from_db(cursor) -> dict:
    print("\nCarregando mapas de tradução do banco de dados...")
    maps = {}
    
    # 1. Funcionários (CPF -> ID)
    cursor.execute(
        "SELECT eb.cpf, e.id_employee FROM employee e JOIN employee_brazilian eb ON e.id_employee = eb.id_employee WHERE eb.cpf IS NOT NULL")
    maps['employees'] = {row[0]: row[1] for row in cursor.fetchall()}
    print(f"✔ {len(maps['employees'])} funcionários carregados.")
    
    # 2. Contratos de Fornecedores (CNPJ -> ID do Contrato)
    cursor.execute(
        "SELECT p.cnpj, c.id_contract FROM provider p JOIN contract c ON p.id_provider = c.id_provider_supplier WHERE p.cnpj IS NOT NULL")
    maps['contracts'] = {row[0]: row[1] for row in cursor.fetchall()}
    print(f"✔ {len(maps['contracts'])} contratos de fornecedores carregados (CNPJ -> id_contract).")
    
    # 3. Contratos de Subcontratados (CNPJ -> ID do Contrato)
    cursor.execute(
        "SELECT p.cnpj, c.id_contract FROM provider p JOIN contract c ON p.id_provider = c.id_provider_subcontractor WHERE p.cnpj IS NOT NULL")
    maps['subcontractors'] = {row[0]: row[1] for row in cursor.fetchall()}
    print(f"✔ {len(maps['subcontractors'])} contratos de subcontratados carregados (CNPJ -> id_contract).")
    
    # 4. Matriz de Documentos (Nome -> ID)
    cursor.execute("SELECT name, id_document, type FROM document_matrix")
    maps['doc_matrix'] = {remove_accents(name).lower(): (id_doc, type_doc) for name, id_doc, type_doc in
                          cursor.fetchall()}
    print(f"✔ {len(maps['doc_matrix'])} tipos de documentos (matriz) carregados.")
    
    # 5. Links de Documentos Existentes (Para evitar recriação)
    # Funcionários
    cursor.execute(
        "SELECT de.id_employee, d.id_document, de.id_documentation FROM document_employee de JOIN document d ON de.id_documentation = d.id_documentation")
    maps['doc_employee_links'] = {(emp_id, doc_id): doc_emp_id for emp_id, doc_id, doc_emp_id in cursor.fetchall()}
    print(f"✔ {len(maps['doc_employee_links'])} links de documentos de funcionários carregados.")
    
    # Contratos
    cursor.execute(
        "SELECT dc.id_contract, d.id_document, dc.id_documentation FROM document_contract dc JOIN document d ON dc.id_documentation = d.id_documentation")
    maps['doc_contract_links'] = {(contract_id, doc_id): doc_contract_id for contract_id, doc_id, doc_contract_id in cursor.fetchall()}
    print(f"✔ {len(maps['doc_contract_links'])} links de documentos de contratos carregados.")
    
    # Subcontratados (mantido para o caso de documentos que vinculam diretamente ao subcontratado)
    cursor.execute(
        "SELECT dpsc.id_provider_subcontractor, d.id_document, dpsc.id_documentation FROM document_provider_subcontractor dpsc JOIN document d ON dpsc.id_documentation = d.id_documentation")
    maps['doc_subcontractor_links'] = {(sub_id, doc_id): doc_sub_id for sub_id, doc_id, doc_sub_id in cursor.fetchall()}
    print(f"✔ {len(maps['doc_subcontractor_links'])} links de documentos de subcontratados carregados.")
    
    return maps


# Função para criar o registro de metadados (document_matrix) no banco
def create_document_record(session, subject_type, subject_id, doc_matrix_id, doc_matrix_type, doc_name_excel):
    create_url = f"{APP_URL}/document/{subject_type}"
    dto_payload = {
        "title": doc_name_excel,
        "type": doc_matrix_type,
        "documentMatrixId": doc_matrix_id,
        subject_type: subject_id
    }
    part_name_map = {
        'employee': 'documentEmployeeRequestDto',
        'contract': 'documentContractRequestDto', # Novo tipo de sujeito
        'subcontractor': 'documentSubcontractorRequestDto'
    }
    part_name = part_name_map.get(subject_type)
    if not part_name: raise ValueError(f"Tipo de sujeito desconhecido: {subject_type}")

    # A API de criação de registro de documento (document/...) é Multipart/form-data
    multipart_payload = {part_name: (None, json.dumps(dto_payload), 'application/json')}
    r = session.post(create_url, files=multipart_payload)
    r.raise_for_status()
    response_data = r.json()
    new_doc_id = response_data.get('idDocument')
    if not new_doc_id: raise ValueError(
        f"API criou o registro mas não retornou 'idDocument'. Resposta: {response_data}")
    return new_doc_id


# ==============================================================================
# LÓGICA PRINCIPAL - VINCULAÇÃO DE DOCUMENTOS
# ==============================================================================
def link_documents(token: str):
    # O caminho do arquivo de mapa é necessário apenas para a leitura do Excel
    base_folder_path = os.path.dirname(os.path.realpath(__file__))
    map_file_path = os.path.join(base_folder_path, MAP_FILE_NAME)

    created, fail, skipped, matrix_created = 0, 0, 0, 0
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
            col_path = 'caminho' # Usado apenas para verificar a existência do arquivo
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

                # Verifica se o arquivo existe (apenas para garantir que o registro é válido)
                file_path = os.path.join(base_folder_path, path_excel.replace('\\', os.sep))
                if not os.path.exists(file_path):
                    print(f"↷ Linha {idx + 2}: Arquivo '{file_path}' não encontrado. Ignorando.")
                    skipped += 1
                    continue

                subject_id, subject_type = (None, None)
                
                # Lógica de vinculação (Corrigida para usar ID do Contrato)
                if cpf and cpf in maps['employees']:
                    subject_id, subject_type = (maps['employees'][cpf], 'employee')
                elif cnpj:
                    # Tenta vincular ao Contrato de Subcontratado
                    if cnpj in maps['subcontractors']:
                        subject_id, subject_type = (maps['subcontractors'][cnpj], 'contract')
                    # Tenta vincular ao Contrato de Fornecedor Principal
                    elif cnpj in maps['contracts']:
                        subject_id, subject_type = (maps['contracts'][cnpj], 'contract')

                if not subject_id:
                    print(f"↷ Linha {idx + 2}: Sujeito (CPF/CNPJ) não encontrado ou não mapeado para ID de Contrato/Funcionário. Ignorando.")
                    skipped += 1
                    continue

                # --- LÓGICA DE CRIAÇÃO DE DOCUMENT_MATRIX ---
                doc_matrix_data = maps['doc_matrix'].get(remove_accents(doc_name_excel).lower())

                if not doc_matrix_data:
                    # Lógica para criar a document_matrix se não existir
                    try:
                        new_matrix_id = str(uuid.uuid4())
                        default_doc_type = 'geral'
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

                # --- VERIFICAÇÃO DE LINK EXISTENTE ---
                id_documentation = None
                link_key = (subject_id, doc_matrix_id)
                
                if subject_type == 'employee':
                    id_documentation = maps['doc_employee_links'].get(link_key)
                elif subject_type == 'contract':
                    id_documentation = maps['doc_contract_links'].get(link_key)
                # Note: 'subcontractor' é tratado como 'contract' na lógica acima, 
                # mas mantemos o mapa 'doc_subcontractor_links' para referência.

                if id_documentation:
                    print(f"↷ Link de doc para '{doc_name_excel}' já existe. Ignorando criação.")
                    created += 1 # Contabiliza como sucesso, pois o link existe
                    continue

                # --- CRIAÇÃO DO NOVO LINK DE VINCULAÇÃO ---
                try:
                    print(f"INFO: Link de doc para '{doc_name_excel}' não existe. Criando via API...")
                    new_id = create_document_record(s, subject_type, subject_id, doc_matrix_id, doc_matrix_type,
                                                    doc_name_excel)
                    print(f"✔ Registro de documento criado com ID: {new_id[:8]}")
                    
                    # Atualiza o mapa para evitar duplicidade na mesma execução
                    if subject_type == 'employee':
                        maps['doc_employee_links'][link_key] = new_id
                    elif subject_type == 'contract':
                        maps['doc_contract_links'][link_key] = new_id
                        
                    created += 1
                except Exception as e:
                    print(f"✖ Falha ao criar registro de doc para '{doc_name_excel}': {e}")
                    fail += 1
                    continue

    except Exception as e:
        print(f"✖ ERRO FATAL: {e}")
    finally:
        if conn and conn.is_connected():
            conn.close()
            print("\nConexão com o banco de dados encerrada.")

    print_summary("Resumo da Vinculação de Documentos", created, fail, skipped, matrix_created)


# ==============================================================================
# EXECUÇÃO PRINCIPAL
# ==============================================================================
if __name__ == "__main__":
    try:
        api_token = login()
        link_documents(api_token)
    except Exception as e:
        print(f"\nOcorreu um erro inesperado na execução do script: {e}")
