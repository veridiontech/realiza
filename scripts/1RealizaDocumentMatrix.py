from sqlite3 import IntegrityError

import pandas as pd
import mysql.connector
from mysql.connector import errorcode
import uuid
import os

# Configurações de conexão com o banco de dados MySQL
DB_CONFIG = {
    "host": "177.170.30.9",
    "port": 8004,
    "user": "root",
    "password": "08Valente$$",
    "database": "dbrealiza"
}

def importar_excel():
    base_dir = os.path.dirname(__file__)
    path = os.path.join(base_dir, "BL_SISTEMA NOVO_PARAMETRIZACOES (1).xlsx")
    try:
        dados = pd.read_excel(path, sheet_name="MATRIZ GERAL DE DOCTOS", engine='openpyxl')
        print("Dados importados com sucesso!")
        return dados
    except Exception as e:
        print("Erro ao importar o Excel:", e)
        return None

def inserir_dados_no_bd(dados):
    try:
        # Conexão com o banco de dados
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("Conexão com o banco de dados estabelecida.")

        # Inserção linha a linha
        for _, linha in dados.iterrows():
            try:
                group_name = linha['Grupo']
                subgroup_name = linha['Subgrupo']
                document_name = linha['Documento']
                expiration = linha['Validade'] if not pd.isna(linha['Validade']) else None
                doc_type = linha['Tipo']
                does_block = 1 if linha['Bloqueio'] == 'Bloqueia' else 0

                # Inserir ou obter o ID do grupo
                cursor.execute(
                    "SELECT id_document_group FROM document_matrix_group WHERE group_name = %s",
                    (group_name,)
                )
                group_row = cursor.fetchone()
                if group_row:
                    group_id = group_row[0]
                else:
                    group_id = str(uuid.uuid4())  # Gerar um ID único
                    cursor.execute(
                        "INSERT INTO document_matrix_group (id_document_group, group_name, creation_date) VALUES (%s, %s, NOW())",
                        (group_id, group_name)
                    )
                    print(f"Grupo '{group_name}' inserido com sucesso.")

                # Inserir ou obter o ID do subgrupo
                cursor.execute(
                    "SELECT id_document_subgroup FROM document_matrix_subgroup WHERE subgroup_name = %s AND id_document_group = %s",
                    (subgroup_name, group_id)
                )
                subgroup_row = cursor.fetchone()
                if subgroup_row:
                    subgroup_id = subgroup_row[0]
                else:
                    subgroup_id = str(uuid.uuid4())  # Gerar um ID único
                    cursor.execute(
                        "INSERT INTO document_matrix_subgroup (id_document_subgroup, subgroup_name, id_document_group, creation_date) "
                        "VALUES (%s, %s, %s, NOW())",
                        (subgroup_id, subgroup_name, group_id)
                    )
                    print(f"Subgrupo '{subgroup_name}' inserido com sucesso.")

                # Inserir documento
                cursor.execute(
                    "SELECT id_document FROM document_matrix WHERE name = %s AND id_document_subgroup = %s",
                    (document_name, subgroup_id)
                )
                doc_row = cursor.fetchone()
                if doc_row:
                    id_doc = doc_row[0]
                else:
                    id_doc = str(uuid.uuid4())  # Gerar um ID único
                    cursor.execute(
                        """
                        INSERT INTO document_matrix (creation_date, expiration_date_amount, name, id_document_subgroup, type, id_document, does_block)
                        VALUES (NOW(), %s, %s, %s, %s, %s, %s)
                        """,
                        (expiration, document_name, subgroup_id, doc_type, id_doc, does_block)
                    )
                    print(f"Documento '{document_name}' inserido com sucesso.")

            except IntegrityError as duplicate_error:
                # Tratamento para entradas duplicadas
                print(f"Entrada duplicada ignorada: {duplicate_error}")
                continue  # Pular para o próximo registro

        # Confirmar alterações no banco
        conn.commit()
        print("Todos os dados foram inseridos no banco com sucesso!")

    except mysql.connector.Error as err:
        if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
            print("Erro de acesso: Verifique o usuário e senha do banco de dados.")
        elif err.errno == errorcode.ER_BAD_DB_ERROR:
            print("Banco de dados não encontrado.")
        else:
            print(f"Erro no banco de dados: {err}")
    finally:
        # Fechar conexão
        if conn:
            cursor.close()
            conn.close()
            print("Conexão com o banco de dados encerrada.")

def main():
    dados = importar_excel()
    if dados is not None:
        inserir_dados_no_bd(dados)

if __name__ == "__main__":
    main()
