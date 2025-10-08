#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import mysql.connector
import uuid
from datetime import datetime

# ==============================================================================
# CONFIGURAÇÕES GERAIS
# ==============================================================================
DB_CONFIG = {
    "host": "177.170.30.9",
    "port": 8004,
    "user": "veridion_user",
    "password": "SenhaSegura123!",
    "database": "dbrealiza"
}


# ==============================================================================
# LÓGICA PRINCIPAL
# ==============================================================================
def link_employee_docs_to_contracts():
    """
    Busca documentos de funcionários e seus contratos, e insere as
    associações faltantes na tabela 'contract_document'.
    """
    conn = None
    print("Iniciando o script de associação de documentos a contratos...")

    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("✔ Conectado ao banco de dados com sucesso.")

        # 1. Buscar todas as associações que JÁ EXISTEM para não duplicar
        cursor.execute("SELECT id_contract, id_documentation FROM contract_document")
        existing_links = set(cursor.fetchall())
        print(f"✔ Encontrados {len(existing_links)} links existentes em 'contract_document'.")

        # 2. Buscar todas as associações que DEVERIAM EXISTIR
        query = """
            SELECT DISTINCT
                ce.id_contract,
                de.id_documentation
            FROM
                contract_employee ce
            JOIN
                document_employee de ON ce.id_employee = de.id_employee
        """
        cursor.execute(query)
        potential_links = cursor.fetchall()
        print(f"✔ Encontrados {len(potential_links)} links potenciais cruzando funcionários e documentos.")

        # 3. Filtrar apenas os links que são NOVOS
        new_links = []
        for contract_id, doc_id in potential_links:
            if (contract_id, doc_id) not in existing_links:
                new_links.append((contract_id, doc_id))

        if not new_links:
            print("\nNenhuma nova associação a ser criada. A tabela já está atualizada.")
            return

        print(f"✔ {len(new_links)} novas associações serão inseridas.")

        # 4. Preparar os dados para a inserção em massa
        # --- ALTERADO: 'PENDENTE' trocado pelo valor numérico 0 ---
        default_status = 0  # 0 geralmente corresponde a PENDENTE
        now = datetime.now()

        data_to_insert = [
            (str(uuid.uuid4()), contract_id, doc_id, now, default_status)
            for contract_id, doc_id in new_links
        ]

        # 5. Executar a inserção em massa
        sql_insert = """
            INSERT INTO contract_document 
                (id, id_contract, id_documentation, created_at, status) 
            VALUES (%s, %s, %s, %s, %s)
        """
        cursor.executemany(sql_insert, data_to_insert)
        conn.commit()

        print(f"\n✔ Operação concluída! {cursor.rowcount} novas linhas foram inseridas em 'contract_document'.")

    except mysql.connector.Error as err:
        print(f"\n✖ ERRO de banco de dados: {err}")
        if conn:
            conn.rollback()  # Desfaz a operação em caso de erro
    finally:
        if conn and conn.is_connected():
            conn.close()
            print("Conexão com o banco de dados encerrada.")


# ==============================================================================
# EXECUÇÃO PRINCIPAL
# ==============================================================================
if __name__ == "__main__":
    link_employee_docs_to_contracts()