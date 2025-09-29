import uuid
import mysql.connector
from mysql.connector import errorcode
import os
import pandas as pd

# Configurações de conexão com o banco de dados MySQL
# DB_CONFIG = {
#     "host": "35.184.183.88",
#     "port": 3306,
#     "user": "veridion-admin-user",
#     "password": "uMsgC-x+uAA]yRG1",
#     "database": "realiza_mysql_development"
# }
DB_CONFIG = {
    "host": "177.170.30.9",
    "port": 8004,
    "user": "veridion_user",
    "password": "SenhaSegura123!",
    "database": "dbrealiza"
}

def popular_ia_additional_prompt():
    try:
        caminho_excel = "BL_MATRIZ GERAL DOCTOS_PARMETRIZACAO_IA_PROMPT.xlsx"
        # Lê o Excel, focando apenas nas colunas que você precisa (A e L)
        # Atenção: ajuste os nomes 'NOME DO DOCUMENTO' e 'DETALHES A VERIFICAR'
        # se os nomes das colunas no seu arquivo forem diferentes.
        df = pd.read_excel(caminho_excel, usecols="A,L", names=['nome_documento', 'prompt_detalhes'])
        print(f"Arquivo Excel '{caminho_excel}' lido com sucesso. {len(df)} linhas encontradas.")
    except FileNotFoundError:
        print(f"✖ ERRO: O arquivo '{caminho_excel}' não foi encontrado na pasta.")
        return
    except Exception as e:
        print(f"✖ ERRO: Falha ao ler o arquivo Excel: {e}")
        return

    updates, inserts, not_found, errors = 0, 0, 0, 0

    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("Conectado ao banco de dados com sucesso.")

        for index, row in df.iterrows():
            nome_doc_excel = row['nome_documento']
            prompt_texto = row['prompt_detalhes']

            # Garante que o prompt não seja um valor nulo (NaN) do pandas
            if pd.isna(prompt_texto):
                prompt_texto = ""

            try:
                # 1. Encontrar o ID do documento pelo nome
                # !!! IMPORTANTE: Troque 'name' pelo nome real da coluna
                # na sua tabela 'document_matrix' que guarda o nome do documento.
                cursor.execute(
                    "SELECT id_document FROM document_matrix WHERE name = %s",
                    (nome_doc_excel,)
                )
                resultado = cursor.fetchone()

                if not resultado:
                    print(f"⚠️  Documento '{nome_doc_excel}' não encontrado no banco. Ignorado.")
                    not_found += 1
                    continue

                id_document = resultado[0]

                # 2. Verificar se já existe um prompt para esse ID
                cursor.execute(
                    "SELECT id FROM ia_additional_prompt WHERE document_matrix_id_document = %s",
                    (id_document,)
                )
                prompt_existente = cursor.fetchone()

                if prompt_existente:
                    # 3. Se existe, ATUALIZA (UPDATE)
                    cursor.execute(
                        "UPDATE ia_additional_prompt SET description = %s WHERE document_matrix_id_document = %s",
                        (prompt_texto, id_document)
                    )
                    print(f"🔄 Prompt atualizado para o documento '{nome_doc_excel}'")
                    updates += 1
                else:
                    # 4. Se não existe, INSERE (INSERT)
                    id_prompt = str(uuid.uuid4())
                    cursor.execute(
                        "INSERT INTO ia_additional_prompt (id, description, document_matrix_id_document) VALUES (%s, %s, %s)",
                        (id_prompt, prompt_texto, id_document)
                    )
                    print(f"✔ Prompt criado para o documento '{nome_doc_excel}'")
                    inserts += 1

            except mysql.connector.Error as err:
                print(f"✖ Erro processando '{nome_doc_excel}': {err}")
                errors += 1
                continue  # Continua para o próximo item do excel

        conn.commit()


    except mysql.connector.Error as err:

        print(f"Erro ao acessar o banco de dados: {err}")

    finally:
        print("\n" + "=" * 50)
        print("Operação concluída!")
        print(
            f"Resumo → Atualizados: {updates} | Criados: {inserts} | Não encontrados no DB: {not_found} | Falhas: {errors}")
        print("=" * 50 + "\n")

        if 'cursor' in locals():
            cursor.close()

        if 'conn' in locals() and conn.is_connected():
            conn.close()
            print("Conexão com o banco encerrada.")

if __name__ == "__main__":
    popular_ia_additional_prompt()
