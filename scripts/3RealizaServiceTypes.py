import openpyxl
import mysql.connector
import uuid
import os
from datetime import datetime

# Configuração de conexão com o banco de dados
DB_CONFIG = {
    "host": "35.184.183.88",
    "port": 3306,
    "user": "veridion-admin-user",
    "password": "uMsgC-x+uAA]yRG1",
    "database": "realiza_mysql_development"
}

# Mapeamento de risco para números
RISK_MAP = {
    'baixo': 1,
    'medio': 2,
    'alto': 3
}

def importar_service_types(path_excel):
    wb = openpyxl.load_workbook(path_excel, data_only=True)
    ws = wb["TIPOS DE SERVIÇO"]

    service_types = []
    for row in ws.iter_rows(min_row=2, max_row=109, min_col=13, max_col=14):
        title = row[0].value
        risk = row[1].value
        if title and risk:
            service_types.append({
                "title": title,
                "risk": risk
            })
    return service_types

def processar_service_types(service_types):
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("Conectado ao banco de dados com sucesso.")

        for service_type in service_types:
            title = service_type["title"]
            risk = service_type["risk"]

            # Verifica se o risco é válido e mapeia para o número
            risk_value = RISK_MAP.get(risk.lower())
            if not risk_value:
                print(f"Risco '{risk}' não válido para o documento '{title}', ignorado.")
                continue  # Se o risco não for válido, pula este documento.

            # Gerar um UUID para o novo tipo de serviço
            service_type_id = str(uuid.uuid4())

            # Inserir na tabela service_type
            cursor.execute(
                """
                INSERT INTO service_type (id_service_type, creation_date, risk, title, service_type)
                VALUES (%s, %s, %s, %s, %s)
                """,
                (service_type_id, datetime.now(), risk_value, title, 'REPOSITORY')  # A coluna 'service_type' é preenchida com 'REPOSITORY'
            )
            print(f"Tipo de serviço '{title}' com risco '{risk}' inserido na tabela service_type.")

            # Inserir na tabela service_type_repo com o id_service_type gerado
            cursor.execute(
                "INSERT INTO service_type_repo (id_service_type) VALUES (%s)",
                (service_type_id,)
            )
            print(f"Tipo de serviço '{title}' inserido na tabela service_type_repo.")

        conn.commit()
        print("Todos os tipos de serviço foram inseridos com sucesso.")

    except mysql.connector.Error as err:
        print(f"Erro no banco de dados: {err}")
    finally:
        if conn:
            cursor.close()
            conn.close()
            print("Conexão com o banco encerrada.")

def main():
    base_dir = os.path.dirname(__file__)
    path = os.path.join(base_dir, "BL_SISTEMA NOVO_PARAMETRIZACOES.xlsx")
    service_types = importar_service_types(path)
    processar_service_types(service_types)

if __name__ == "__main__":
    main()
