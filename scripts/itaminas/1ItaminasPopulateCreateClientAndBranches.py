import pandas as pd
import os
import time
import requests

app_url = "localhost:8080"
user_data = {
    "email": "realiza@assessoria.com",
    "password": "senha123"
}
global client_global_id
global branches
global users
global profileId

def check_empty(value):
    return value if not pd.isna(value) and value != '' else None

def split_fullname(fullname):
    parts = fullname.split()
    first_name = parts[0] if len(parts) > 0 else None
    surname = " ".join(parts[1:]) if len(parts) > 1 else None

    return first_name, surname


def import_data():
    dir_atual = os.path.dirname(os.path.realpath(__file__))
    path = os.path.join(dir_atual, "ITAMINAS_CONFIGURACOES.xlsx")
    try:
        dados = pd.read_excel(path, sheet_name=None, engine='openpyxl', skiprows=1)  # sheet_name=None lê todas as planilhas
        print("Dados importados com sucesso!")
        return dados
    except Exception as e:
        print("Erro ao importar o Excel:", e)
        return None

def login(login_data):
    url = app_url + "/login"
    try:
        response = requests.post(url, json=login_data)

        if response.status_code == 200:
            data = response.json()
            token = data.get("token")
            if token:
                print("Login bem-sucedido. Token recebido:", token)
                return token
            else:
                print("Token não encontrado na resposta.")
                return None
    except Exception as e:
        print(f"Erro ao tentar realizar o login: {e}")
        return None

def create_client(token, data):
    criar atividades
    url = app_url + "/client"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    params = {
        "profilesFromRepo": True
    }
    try:
        client_data = data.get('CLIENTE', None)
        if client_data is not None and not client_data.empty:
            corporate_name = check_empty(client_data.iloc[0]['Razão Social*'])
            trade_name = check_empty(client_data.iloc[0]['Nome Fantasia'])
            cnpj = check_empty(client_data.iloc[0]['CNPJ*'])
            address = check_empty(client_data.iloc[0]['Endereço'])
            email = check_empty(client_data.iloc[0]['E-mail'])
            cep = check_empty(client_data.iloc[0]['CEP'])
            city = check_empty(client_data.iloc[0]['Cidade'])
            state = check_empty(client_data.iloc[0]['Estado'])
            telephone = check_empty(client_data.iloc[0]['Telefone-1'])

            client = {
                "corporateName": corporate_name,
                "tradeName": trade_name,
                "cnpj": cnpj,
                "address": address,
                "email": email,
                "cep": cep,
                "city": city,
                "state": state,
                "telephone": telephone
            }

            # Enviando a requisição POST para criar o cliente
            response = requests.post(url, json=client, params=params, headers=headers)

            if response.status_code == 200:
                # Caso a requisição seja bem-sucedida
                data = response.json()
                client_global_id = data["idClient"]
                print("Cliente criado com sucesso:", data["corporateName"])
                return data
            else:
                print(f"Falha ao criar cliente. Status Code: {response.status_code}")
                print("Resposta:", response.text)
                return None
    except Exception as e:
        print(f"Erro ao criar cliente: {e}")
        return None

def create_branches(token, cliente, data):
    url = app_url + "/branch"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    params = {
        "profilesFromRepo": True
    }
    try:
        units_data = data.get('UNIDADES', None)
        if units_data is not None and not units_data.empty:
            for _, branch in units_data.iterrows():
                if branch:
                    name = check_empty(branch['Unidade*'])
                    cnpj = check_empty(branch['CNPJ*'])
                    address = check_empty(branch['Endereço*'])
                    number = check_empty(branch['Número*'])
                    cep = check_empty(branch['CEP*'])
                    city = check_empty(branch['Cidade*'])
                    state = check_empty(branch['Estado*'])
                    telephone = check_empty(branch['Telefone-1'])

                    branch_request = {
                        "name": name,
                        "cnpj": cnpj,
                        "address": address,
                        "number": number,
                        "cep": cep,
                        "city": city,
                        "state": state,
                        "telephone": telephone,
                        "cliente": cliente["idClient"],
                        "replicateFromBase": True
                    }

                    # Enviando a requisição POST para criar o cliente
                    response = requests.post(url, json=branch_request, params=params, headers=headers)

                    if response.status_code == 200:
                        # Caso a requisição seja bem-sucedida
                        data = response.json()
                        print("Filial criada com sucesso:", data["name"])
                        branches[data["name"]] = data["idBranch"]
                        return data
                    else:
                        print(f"Falha ao criar filial. Status Code: {response.status_code}")
                        print("Resposta:", response.text)
                        return None
        else:
            print("Dados de unidades não encontrados ou vazios.")
    except Exception as e:
        print(f"Erro ao criar filial: {e}")
        return None


def create_users(token, branch_hash_map, data):
    url = app_url + "/user/manager/new-user"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    try:
        response = requests.get(app_url + f"/profile/by-name/{client_global_id}", headers=headers)
        if response.status_code == 200:
            profiles = response.json()
            desired_profile_name = "Padrão"
            global profileId
            profileId = next((profile["id"] for profile in profiles if profile["profileName"] == desired_profile_name),
                             None)

            if profileId:
                print(f"Profile ID encontrado: {profileId}")
            else:
                criar profile
                print("Profile não encontrado.")
                return
        else:
            print(f"Falha ao buscar o perfil. Status Code: {response.status_code}")
            print("Resposta:", response.text)

        users_data = data.get('USUARIOS', None)
        if users_data is not None and not users_data.empty:
            for _, user in users_data.iterrows():
                if user:
                    full_name = check_empty(user['Funcionário*'])
                    cpf = check_empty(user['CPF*'])
                    email = check_empty(user['E-mail*'])
                    branch = branch_hash_map.get(check_empty(user['Unidade*']))

                    first_name, surname = split_fullname(full_name)

                    user_request = {
                        "cpf": cpf,
                        "firstName": first_name,
                        "surname": surname,
                        "email": email,
                        "branch": branch,
                        "role": 'ROLE_CLIENT_MANAGER',
                        "enterprise": 'CLIENT',
                        "idEnterprise": client_global_id,
                        "profileId": profileId
                    }

                    response = requests.post(url, json=user_request, headers=headers)

                    if response.status_code == 200:
                        print("Usuário criado com sucesso:", response.text)
                        return data
                    else:
                        print(f"Falha ao criar usuário. Status Code: {response.status_code}")
                        print("Resposta:", response.text)
                        return None
        else:
            print("Dados de usuários não encontrados ou vazios.")
    except Exception as e:
        print(f"Erro ao criar usuário: {e}")
        return None


def main():
    file_data = import_data()
    if file_data is not None:
        token = login(user_data)
        if token is not None:
            client = create_client(token, file_data)
            if client is not None:
                create_branches(token, client, file_data)
                print("Esperando 20 minutos antes de rodar o restow...")
                time.sleep(20 * 60)
                create_users(token, branches, file_data)
                create_supplier(token, file_data)
                create_contracts(token, suppliers, users, file_data)

if __name__ == "__main__":
    main()
