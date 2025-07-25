import uuid
import csv
import os
import mysql.connector

# Configuração da conexão com o banco de dados
DB_CONFIG = {
    "host": "35.184.183.88",
    "port": 3306,
    "user": "veridion-admin-user",
    "password": "uMsgC-x+uAA]yRG1",
    "database": "realiza_mysql_development"
}

# Caminho do arquivo CSV
BASE_DIR = os.path.dirname(__file__)
CSV_PATH = os.path.join(BASE_DIR, "CBO2002 - Ocupacao.csv")

def importar_cbo(csv_path):
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("Conectado ao banco de dados com sucesso.")

        with open(csv_path, newline='', encoding='latin1') as csvfile:
            reader = csv.DictReader(csvfile, delimiter=';')
            print(f"Campos encontrados: {reader.fieldnames}")
            for row in reader:
                codigo = row['CODIGO'].strip()
                titulo = row['TITULO'].strip()

                # Verifica se o código já existe
                cursor.execute("SELECT code FROM cbo WHERE code = %s", (codigo,))
                if cursor.fetchone():
                    print(f"Registro com código {codigo} já existe. Ignorado.")
                    continue

                # Insere no banco de dados
                id_gerado = str(uuid.uuid4())
                cursor.execute(
                    "INSERT INTO cbo (id, code, title) VALUES (%s, %s, %s)",
                    (id_gerado, codigo, titulo)
                )
                print(f"Inserido: {codigo} - {titulo}")

        conn.commit()
        print("Todos os dados foram inseridos com sucesso.")

    except mysql.connector.Error as err:
        print(f"Erro ao acessar o banco de dados: {err}")
    except FileNotFoundError:
        print(f"Arquivo não encontrado: {csv_path}")
    except Exception as e:
        print(f"Erro inesperado: {e}")
    finally:
        if 'cursor' in locals():
            cursor.close()
        if 'conn' in locals() and conn.is_connected():
            conn.close()
            print("Conexão com o banco encerrada.")

if __name__ == "__main__":
    importar_cbo(CSV_PATH)
