import uuid
import mysql.connector
from mysql.connector import errorcode
import os

# Configurações de conexão com o banco de dados MySQL
DB_CONFIG = {
    "host": "35.184.183.88",
    "port": 3306,
    "user": "veridion-admin-user",
    "password": "uMsgC-x+uAA]yRG1",
    "database": "realiza_mysql_development"
}

def popular_ia_additional_prompt():
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("Conectado ao banco de dados com sucesso.")

        # Buscar todos os documentos
        cursor.execute("SELECT id_document FROM document_matrix")
        documentos = cursor.fetchall()

        if not documentos:
            print("Nenhum documento encontrado na tabela document_matrix.")
            return

        for (id_document,) in documentos:
            id_prompt = str(uuid.uuid4())

            # Verifica se já existe um prompt para esse documento
            cursor.execute(
                "SELECT id FROM ia_additional_prompt WHERE document_matrix_id_document = %s",
                (id_document,)
            )
            if cursor.fetchone():
                print(f"Prompt já existente para o documento {id_document}. Ignorado.")
                continue

            # Insere novo prompt
            cursor.execute(
                "INSERT INTO ia_additional_prompt (id, description, document_matrix_id_document) VALUES (%s, %s, %s)",
                (id_prompt, "", id_document)
            )
            print(f"Prompt criado para documento {id_document}")

        conn.commit()
        print("Todos os prompts foram inseridos com sucesso!")

    except mysql.connector.Error as err:
        print(f"Erro ao acessar o banco de dados: {err}")
    finally:
        if 'cursor' in locals():
            cursor.close()
        if 'conn' in locals() and conn.is_connected():
            conn.close()
            print("Conexão com o banco encerrada.")

if __name__ == "__main__":
    popular_ia_additional_prompt()
