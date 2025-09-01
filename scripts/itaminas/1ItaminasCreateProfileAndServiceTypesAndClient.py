import pandas as pd
import os
import requests

app_url = "http://localhost:8080"
user_data = {
    "email": "realiza@assessoria.com",
    "password": "senha123"
}

def check_empty(value):
    return value if not pd.isna(value) and value != '' else None

def import_data(file_name):
    dir_atual = os.path.dirname(os.path.realpath(__file__))
    path = os.path.join(dir_atual, file_name)
    try:
        dados = pd.read_excel(path, sheet_name=None, engine='openpyxl',
            dtype=str,             # <- aqui
            keep_default_na=False  # células vazias viram '' em vez de NaN
        )
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

def create_service_types(token, data):
    url = app_url + "/contract/service-type"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    try:
        service_data = data.get('Resultado da consulta', None)
        if service_data is None or service_data.empty:
            print("Dados de serviços não encontrados ou vazios.")
            return

        ok = already = fail = 0

        for _, service in service_data.iterrows():
            if service.empty:
                continue

            name = check_empty(service.get('Tipo do Serviço Único'))
            if not name:
                continue

            risk = check_empty(service.get('Risco do Serviço Único'))
            if risk == "Alto":
                risk = "HIGH"
            elif risk == "Médio":
                risk = "MEDIUM"
            elif risk == "Baixo":
                risk = "LOW"
            else:
                # se vier vazio ou diferente, pode deixar None (ou defina um default)
                risk = "LOW"

            payload = {"name": name, "risk": risk}

            # 1) checa existência
            try:
                resp = requests.get(url + "/check-by-name", params={"name": name}, headers=headers)
                # algumas APIs retornam boolean, outras string "True"/"False"
                exists = (resp.json() is True) or (str(resp.json()).lower() == "true")
                print("Service checado com sucesso:", name)
            except Exception as e:
                print(f"✖ falha ao checar service '{name}': {e}")
                fail += 1
                continue  # <<< não para o script

            # 2) cria se não existir
            if not exists:
                response = requests.post(url + "/repository", json=payload, headers=headers)
                if response.status_code == 200:
                    data = response.json()
                    print("✔ service criado com sucesso:", data.get("name", name))
                    ok += 1
                elif response.status_code == 409:
                    print("↷ service já existia (409):", name)
                    already += 1
                else:
                    print(f"✖ falha ao criar service ({response.status_code}): {name}")
                    print("Resposta:", response.text[:200])
                    fail += 1
                    continue  # <<< segue para o próximo
            else:
                print("↷ service já existente:", name)
                already += 1

        print(f"\nResumo → criados: {ok} | existentes: {already} | falhas: {fail}")

    except Exception as e:
        print(f"Erro ao criar service: {e}")

def create_profile(token):
    url = app_url + "/profile/repo/check-by-name"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    params = {
        "name": "Padrão"
    }

    try:
        exists = requests.get(url, params=params, headers=headers)
        if not exists.json():
            print("Perfil padrão não existente no repositório. Criando...")
            response = requests.post(app_url + "/profile/repo", json= {
                "name": "Padrão",
                "description": "Perfil padrão com todas as autorizações",
                "admin": True
            }, headers=headers)
            if response.status_code == 200:
                # Caso a requisição seja bem-sucedida
                data = response.json()
                print("Perfil padrão criada com sucesso:", data["name"])
                return data
            else:
                print(f"Falha ao criar perfil padrão. Status Code: {response.status_code}")
                print("Resposta:", response.text)
                return None
        else:
            print("Perfil padrão já existente no repositório.")
    except Exception as e:
        print(f"Erro ao criar perfil padrão: {e}")
        return None
    return None

def create_client(token, data):
    global client_global_id
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
            telephone = check_empty(client_data.iloc[0]['Telefone - 1'])

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
                print(client_global_id)
                return data
            else:
                print(f"Falha ao criar cliente. Status Code: {response.status_code}")
                print("Resposta:", response.text)
                return None
    except Exception as e:
        print(f"Erro ao criar cliente: {e}")
        return None

def main():
    token = login(user_data)
    if token is not None:
        file_data = import_data("ITAMINAS_CONFIGURACOES.xlsx")
        new_file_data = import_data("SISTEMA NOVO_ITAMINAS.xlsx")
        if new_file_data is not None:
            create_profile(token)
            create_service_types(token, new_file_data)
        if file_data is not None:
            create_client(token, file_data)

if __name__ == "__main__":
    main()
