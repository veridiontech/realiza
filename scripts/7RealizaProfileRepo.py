import uuid
import mysql.connector
from mysql.connector import errorcode

# Configurações de conexão com o banco de dados MySQL
DB_CONFIG = {
    "host": "177.170.30.9",
    "port": 8004,
    "user": "root",
    "password": "08Valente$$",
    "database": "dbrealiza"
}

# Lista de perfis para inspetor e gestor
nomes = ['João', 'Maria', 'Carlos', 'Ana', 'Pedro']

def preencher_perfis():
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

        id_profile = str(uuid.uuid4())


        if cursor.fetchone():
            print(f"Prompt já existente para o documento {id_document}. Ignorado.")
            continue

        # Preencher para o perfil "Admin"
        cursor.execute(
            "INSERT INTO user_profile_repo (id, admin, description, document_viewer, environment, general, health, inspector, laboral, manager, name, registration_and_certificates, registration_contract, registration_user, viewer, workplace_safety, document_matrix_id_document) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
            (id_profile, True, "Admin profile", False, False, False, False, False, False, False, "Admin", False, False, False, False, False, id_document)
        )
        print(f"Perfil Admin criado para o documento {id_document}")

        # Preencher para os perfis de "Inspetor" (um por vez, cada um com True)
        for nome in nomes:
            inspetor = str(uuid.uuid4())
            cursor.execute(
                "INSERT INTO user_profile_repo (id, admin, description, document_viewer, environment, general, health, inspector, laboral, manager, name, registration_and_certificates, registration_contract, registration_user, viewer, workplace_safety, document_matrix_id_document) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                (inspetor, False, f"Inspetor profile {nome}", True, False, False, False, True, False, False, nome, False, False, False, False, False, id_document)
            )
            print(f"Perfil Inspetor criado para o documento {id_document} com o nome {nome}")

        # Preencher para os perfis de "Gestor" (um por vez, cada um com True)
        for nome in nomes:
            gestor = str(uuid.uuid4())
            cursor.execute(
                "INSERT INTO user_profile_repo (id, admin, description, document_viewer, environment, general, health, inspector, laboral, manager, name, registration_and_certificates, registration_contract, registration_user, viewer, workplace_safety, document_matrix_id_document) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                (gestor, False, f"Gestor profile {nome}", True, False, False, False, False, False, True, nome, False, False, False, False, False, id_document)
            )
            print(f"Perfil Gestor criado para o documento {id_document} com o nome {nome}")

        # Preencher para o perfil "Viewer"
        viewer = str(uuid.uuid4())
        cursor.execute(
            "INSERT INTO user_profile_repo (id, admin, description, document_viewer, environment, general, health, inspector, laboral, manager, name, registration_and_certificates, registration_contract, registration_user, viewer, workplace_safety, document_matrix_id_document) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
            (viewer, False, "Viewer profile", True, False, False, False, False, False, False, "Viewer", False, False, False, True, False, id_document)
        )
        print(f"Perfil Viewer criado para o documento {id_document}")

        # Preencher para o perfil "Gestor Geral" (todos True, exceto Admin e Viewer)
        gestor_geral = str(uuid.uuid4())
        cursor.execute(
            "INSERT INTO user_profile_repo (id, admin, description, document_viewer, environment, general, health, inspector, laboral, manager, name, registration_and_certificates, registration_contract, registration_user, viewer, workplace_safety, document_matrix_id_document) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
            (gestor_geral, False, "Gestor Geral profile", True, True, True, True, True, True, True, "Gestor Geral", True, True, True, False, True, id_document)
        )
        print(f"Perfil Gestor Geral criado para o documento {id_document}")

        conn.commit()
        print("Todos os perfis foram inseridos com sucesso!")

    except mysql.connector.Error as err:
        print(f"Erro ao acessar o banco de dados: {err}")
    finally:
        if 'cursor' in locals():
            cursor.close()
        if 'conn' in locals() and conn.is_connected():
            conn.close()
            print("Conexão com o banco encerrada.")

if __name__ == "__main__":
    preencher_perfis()
