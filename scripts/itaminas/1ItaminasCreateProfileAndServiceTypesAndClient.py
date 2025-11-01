#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pandas as pd
import os
import requests

app_url = "https://realiza.onrender.com"
user_data = {
    "email": "realiza@assessoria.com",
    "password": "senha123"
}

# Variável global para armazenar o ID do cliente criado
client_global_id = None 

def check_empty(value ):
    return value if not pd.isna(value) and value != '' else None

def import_data(file_name):
    dir_atual = os.path.dirname(os.path.realpath(__file__))
    path = os.path.join(dir_atual, file_name)
    try:
        # Tenta ler as abas 'CLIENTE' e 'Resultado da consulta'
        dados = pd.read_excel(path, sheet_name=None, engine='openpyxl',
            dtype=str,             # <- aqui
            keep_default_na=False  # células vazias viram '' em vez de NaN
        )
        print(f"✔ Dados de '{file_name}' importados com sucesso!")
        return dados
    except Exception as e:
        print(f"✖ Erro ao importar o Excel '{file_name}': {e}")
        return None

def login(login_data):
    url = app_url + "/login"
    try:
        response = requests.post(url, json=login_data)
        if response.status_code == 200:
            data = response.json()
            token = data.get("token")
            if token:
                print("✔ Login bem-sucedido. Token recebido.")
                return token
            else:
                print("✖ Token não encontrado na resposta.")
                return None
    except Exception as e:
        print(f"✖ Erro ao tentar realizar o login: {e}")
        return None

def create_service_types(token, data):
    url = app_url + "/contract/service-type"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    try:
        # Assume que a aba de serviços é 'Resultado da consulta'
        service_data = data.get('Resultado da consulta', None) 
        if service_data is None or service_data.empty:
            print("✖ Dados de serviços não encontrados ou vazios na aba 'Resultado da consulta'.")
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
                risk = "LOW" # Padrão para LOW

            payload = {"name": name, "risk": risk}

            # 1) checa existência (Prevenção de Duplicatas)
            try:
                resp = requests.get(url + "/check-by-name", params={"name": name}, headers=headers)
                exists = (resp.json() is True) or (str(resp.json()).lower() == "true")
            except Exception as e:
                print(f"✖ falha ao checar service '{name}': {e}")
                fail += 1
                continue

            # 2) cria se não existir
            if not exists:
                response = requests.post(url + "/repository", json=payload, headers=headers)
                if response.status_code == 200:
                    data = response.json()
                    print(f"✔ service criado: {data.get('name', name)}")
                    ok += 1
                elif response.status_code == 409:
                    print(f"↷ service já existia (409): {name}")
                    already += 1
                else:
                    print(f"✖ falha ao criar service ({response.status_code}): {name}. Resposta: {response.text[:100]}")
                    fail += 1
            else:
                print(f"↷ service já existente: {name}")
                already += 1

        print(f"\nResumo Tipos de Serviço → criados: {ok} | existentes: {already} | falhas: {fail}")

    except Exception as e:
        print(f"✖ Erro fatal ao criar service types: {e}")

def create_profile(token):
    url_check = app_url + "/profile/repo/check-by-name"
    url_create = app_url + "/profile/repo"
    profile_name = "Padrão"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    params = {"name": profile_name}

    try:
        exists = requests.get(url_check, params=params, headers=headers)
        if not exists.json():
            print(f"INFO: Perfil '{profile_name}' não existente. Criando...")
            response = requests.post(url_create, json= {
                "name": profile_name,
                "description": "Perfil padrão com todas as autorizações",
                "admin": True
            }, headers=headers)
            if response.status_code == 200:
                data = response.json()
                print(f"✔ Perfil '{profile_name}' criado com sucesso.")
                return data
            else:
                print(f"✖ Falha ao criar perfil padrão. Status Code: {response.status_code}. Resposta: {response.text}")
                return None
        else:
            print(f"↷ Perfil '{profile_name}' já existente.")
    except Exception as e:
        print(f"✖ Erro ao criar perfil padrão: {e}")
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
        # Assume que a aba de cliente é 'CLIENTE'
        client_data = data.get('CLIENTE', None) 
        if client_data is None or client_data.empty:
             print("✖ Dados de cliente não encontrados ou vazios na aba 'CLIENTE'.")
             return None

        # Pega a primeira linha da aba CLIENTE
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
            data = response.json()
            client_global_id = data["idClient"]
            print(f"✔ Cliente '{data['corporateName']}' criado com sucesso. ID: {client_global_id}")
            return data
        elif response.status_code == 409:
            print(f"↷ Cliente '{corporate_name}' já existe (409).")
            # Tenta buscar o ID do cliente existente
            # (Lógica simplificada: se já existe, o ID deve ser buscado em um script posterior)
            return {"corporateName": corporate_name} 
        else:
            print(f"✖ Falha ao criar cliente. Status Code: {response.status_code}. Resposta: {response.text}")
            return None
    except Exception as e:
        print(f"✖ Erro fatal ao criar cliente: {e}")
        return None

def main():
    token = login(user_data)
    if token is not None:
        # Arquivo de configuração (para dados do cliente)
        file_data = import_data("ITAMINAS_CONFIGURACOES.xlsx") 
        # Arquivo principal (para tipos de serviço)
        new_file_data = import_data("SISTEMA NOVO_ITAMINAS.xlsx") 
        
        # 1. Cria Perfil Padrão
        create_profile(token) 
        
        # 2. Cria Tipos de Serviço
        if new_file_data is not None:
            create_service_types(token, new_file_data)
        
        # 3. Cria Cliente
        if file_data is not None:
            create_client(token, file_data)

if __name__ == "__main__":
    main()
