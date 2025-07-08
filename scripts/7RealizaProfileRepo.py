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
nomes = ['trabalhista', 'segurança', 'ssma', 'geral', 'saude', 'meio ambiente']

def preencher_perfis():
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("Conectado ao banco de dados com sucesso.")

        # Buscar todos os profiles
        cursor.execute("SELECT name FROM user_profile_repo")
        profiles = cursor.fetchall()

        if not profiles:
            print("Nenhum profile encontrado na tabela user_profile_repo.")

        # Preencher para o perfil "Admin"
        cursor.execute("SELECT name FROM user_profile_repo where name = 'Admin Profile'")
        profile = cursor.fetchall()
        if profiles:
            print("Perfil Admin Profile já existente")
        else:
            id_profile = str(uuid.uuid4())
            cursor.execute(
                "INSERT INTO user_profile_repo ("
                "id, "
                "name, "
                "description, "
                "admin, "
                "viewer, "
                "manager, "
                "inspector, "
                "document_viewer, "
                "registration_user, "
                "registration_contract, "
                "laboral, "
                "workplace_safety,"
                "registration_and_certificates, "
                "general, "
                "health, "
                "environment,"
                "concierge)"
                "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                (id_profile, "Admin profile", "default", True, False, False, False, False, False, False, False, False, False, False, False, False, False)
            )
            print(f"Perfil Admin criado com sucesso")

        # Preencher para os perfis de "Inspetor" (um por vez, cada um com True)
        i = 0
        for nome in nomes:
            cursor.execute(f"SELECT name FROM user_profile_repo where name = 'Inspetor {nome}'")
            profile = cursor.fetchall()
            if profiles:
                print(f"Perfil Inspetor {nome} já existente")
            else:
                id_profile = str(uuid.uuid4())
                listDeTrue = [False,False,False,False,False,False]
                inspetor = str(uuid.uuid4())
                listDeTrue[i] = True
                i = i + 1
                cursor.execute(
                    "INSERT INTO user_profile_repo ("
                    "id, "
                    "name, "
                    "description, "
                    "admin, "
                    "viewer, "
                    "manager, "
                    "inspector, "
                    "document_viewer, "
                    "registration_user, "
                    "registration_contract, "
                    "laboral, "
                    "workplace_safety,"
                    "registration_and_certificates, "
                    "general, "
                    "health, "
                    "environment,"
                    "concierge)"
                    "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    (id_profile, "Inspetor " + nome, "default", False, False, False, True, False, False, False, listDeTrue[0], listDeTrue[1],
                     listDeTrue[2], listDeTrue[3], listDeTrue[4], listDeTrue[5], False)
                )
                print(f"Perfil Inspetor {nome} criado com sucesso")

        # Preencher para os perfis de "Gestor" (um por vez, cada um com True)
        i = 0
        for nome in nomes:
            cursor.execute(f"SELECT name FROM user_profile_repo where name = 'Gestor {nome}'")
            profile = cursor.fetchall()
            if profiles:
                print(f"Perfil Gestor {nome} já existente")
            else:
                id_profile = str(uuid.uuid4())
                listDeTrue = [False, False, False, False, False, False]
                inspetor = str(uuid.uuid4())
                listDeTrue[i] = True
                i = i + 1
                cursor.execute(
                    "INSERT INTO user_profile_repo ("
                    "id, "
                    "name, "
                    "description, "
                    "admin, "
                    "viewer, "
                    "manager, "
                    "inspector, "
                    "document_viewer, "
                    "registration_user, "
                    "registration_contract, "
                    "laboral, "
                    "workplace_safety,"
                    "registration_and_certificates, "
                    "general, "
                    "health, "
                    "environment,"
                    "concierge)"
                    "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    (id_profile, "Gestor " + nome, "default", False, False, True, False, False, False, False,
                     listDeTrue[0], listDeTrue[1],
                     listDeTrue[2], listDeTrue[3], listDeTrue[4], listDeTrue[5], False)
                )
                print(f"Perfil Gestor {nome} criado com sucesso")

        # Preencher para o perfil "Gerente Geral"
        cursor.execute(f"SELECT name FROM user_profile_repo where name = 'Gerente Geral'")
        profile = cursor.fetchall()
        if profiles:
            print(f"Perfil Gerente Geral já existente")
        else:
            id_profile = str(uuid.uuid4())
            cursor.execute(
                "INSERT INTO user_profile_repo ("
                "id, "
                "name, "
                "description, "
                "admin, "
                "viewer, "
                "manager, "
                "inspector, "
                "document_viewer, "
                "registration_user, "
                "registration_contract, "
                "laboral, "
                "workplace_safety,"
                "registration_and_certificates, "
                "general, "
                "health, "
                "environment,"
                "concierge)"
                "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                (
                id_profile, "Gerente geral profile", "default", False, False, True, True, True, True, True, True, True, True,
                True, True, True, False)
            )
            print(f"Perfil Gerente Geral criado com sucesso")

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
