import uuid
import mysql.connector
from mysql.connector import errorcode
import pandas as pd
import os

# ==============================================================================
# CONFIGURAÇÕES DE CONEXÃO
# ==============================================================================
# ATENÇÃO: Verifique se estas configurações de banco de dados ainda são as corretas.
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

# ==============================================================================
# FUNÇÃO PRINCIPAL
# ==============================================================================
def popular_cargos_itaminas():
    """
    Lê um arquivo Excel, extrai os cargos (posições) e os cadastra na tabela
    'Position' do banco de dados, caso ainda não existam.
    """
    # --- 1. LEITURA E PREPARAÇÃO DO ARQUIVO EXCEL ---
    try:
        caminho_excel = "SISTEMA NOVO_ITAMINAS.xlsx"
        print(f"Iniciando leitura do arquivo: '{caminho_excel}'...")

        # Lê apenas a coluna 'V', ignora linhas sem cabeçalho e nomeia a coluna
        df = pd.read_excel(caminho_excel, usecols="V", header=None, names=['cargo'])

        # Limpeza e preparação dos dados:
        # 1. Remove linhas que são completamente vazias (NaN).
        # 2. Converte tudo para string para garantir consistência.
        # 3. Remove espaços em branco no início e no fim de cada cargo.
        # 4. Pega apenas os valores únicos para não processar duplicatas.
        # 5. Converte para uma lista e remove qualquer item que ficou vazio após a limpeza.
        cargos_unicos = df['cargo'].dropna().astype(str).str.strip().unique()
        cargos_unicos = [cargo for cargo in cargos_unicos if cargo]

        if not cargos_unicos:
            print("⚠️ Nenhum cargo válido encontrado na coluna V do arquivo Excel. Encerrando.")
            return

        print(f"✔ Arquivo lido com sucesso. {len(cargos_unicos)} cargos únicos encontrados para processar.")

    except FileNotFoundError:
        print(f"✖ ERRO CRÍTICO: O arquivo '{caminho_excel}' não foi encontrado na pasta 'scripts'.")
        return
    except Exception as e:
        print(f"✖ ERRO CRÍTICO: Falha ao ler ou processar o arquivo Excel: {e}")
        return

    # --- 2. PROCESSAMENTO E INTERAÇÃO COM O BANCO DE DADOS ---
    creates, already_exists, errors = 0, 0, 0
    conn = None
    cursor = None

    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("✔ Conectado ao banco de dados com sucesso.")

        for cargo_title in cargos_unicos:
            try:
                # Verifica se o cargo já existe na tabela Position
                cursor.execute(
                    "SELECT id FROM position WHERE title = %s",
                    (cargo_title,)
                )
                resultado = cursor.fetchone()

                if resultado:
                    # O cargo já existe, apenas loga e continua
                    print(f"↷ Cargo '{cargo_title}' já existia. Ignorado.")
                    already_exists += 1
                else:
                    # O cargo não existe, então cria um novo
                    id_cargo = str(uuid.uuid4())
                    cursor.execute(
                        "INSERT INTO position (id, title) VALUES (%s, %s)",
                        (id_cargo, cargo_title)
                    )
                    print(f"✔ Cargo '{cargo_title}' criado com sucesso.")
                    creates += 1

            except mysql.connector.Error as err:
                print(f"✖ Erro ao processar o cargo '{cargo_title}': {err}")
                errors += 1
                continue  # Pula para o próximo cargo em caso de erro

        # Efetiva todas as inserções no banco de dados
        if creates > 0:
            conn.commit()
            print(f"✔ {creates} novos cargos foram salvos no banco de dados.")

    except mysql.connector.Error as err:
        print(f"✖ ERRO DE CONEXÃO: Não foi possível acessar o banco de dados: {err}")

    finally:
        # --- 3. RESUMO E ENCERRAMENTO ---
        print("\n" + "="*50)
        print("Operação concluída!")
        print(f"Resumo → Criados: {creates} | Já existentes: {already_exists} | Falhas: {errors}")
        print("="*50 + "\n")

        if cursor:
            cursor.close()
        if conn and conn.is_connected():
            conn.close()
            print("Conexão com o banco de dados encerrada.")


# ==============================================================================
# PONTO DE ENTRADA DO SCRIPT
# ==============================================================================
if __name__ == "__main__":
    popular_cargos_itaminas()