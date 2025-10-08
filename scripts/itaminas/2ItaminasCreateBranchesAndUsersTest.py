import pandas as pd
import os
import requests
import hashlib
import time

APP_URL = "http://localhost:8080".rstrip("/")

USER_LOGIN = {
    "email": "realiza@assessoria.com",
    "password": "senha123",
}
global client_global_id
global branches
global users
global profile_id
branches = {}
profile_id = "9dbf8db2-6f0c-476e-bb38-aa4e0b33d821"
client_global_id = "8a291eb6-e627-4215-a622-3aa9fc39300e"

def mask_email(email: str) -> str:
    if not email or "@" not in email:
        return str(email)
    name, dom = email.split("@", 1)
    return (name[:2] + "***@" + dom) if len(name) > 2 else ("***@" + dom)

def checksum(payload: dict) -> str:
    # Útil para rastrear payloads sem expor PII
    h = hashlib.sha1(repr(sorted(payload.items())).encode("utf-8")).hexdigest()
    return h[:10]

def print_summary(title: str, ok: int, already: int, fail: int, skipped: int = 0):
    print(f"\n{title} → criados: {ok} | existentes: {already} | falhas: {fail}" + (f" | ignorados: {skipped}" if skipped else ""))

def check_empty(value):
    return value if not pd.isna(value) and value != '' else None

def split_fullname(fullname):
    parts = fullname.split()
    first_name = parts[0] if len(parts) > 0 else None
    surname = " ".join(parts[1:]) if len(parts) > 1 else None

    return first_name, surname

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

def login():
    url = f"{APP_URL}/login"
    r = requests.post(url, json=USER_LOGIN, timeout=15)
    r.raise_for_status()
    data = r.json()
    token = data.get("token") or data.get("access_token")
    if not token:
        raise RuntimeError(f"Login OK, mas não veio token. Resposta: {data}")
    print("Login ok")
    return token

def create_branches(token, data):
    global branches
    global client_global_id
    url = APP_URL + "/branch"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    ok, already, fail, skipped = 0, 0, 0, 0

    try:
        units_data = data.get('UNIDADES', None)
        if units_data is not None and not units_data.empty:
            for _, branch in units_data.iterrows():
                if not any(str(v).strip() for v in branch.values):
                    skipped += 1
                    continue

                name = check_empty(branch.get('Unidade*'))
                if not name:
                    skipped += 1
                    continue

                cnpj = check_empty(branch['CNPJ*'])
                address = check_empty(branch['Endereço*'])
                number = check_empty(branch['Número*'])
                cep = check_empty(branch['CEP*'])
                city = check_empty(branch['Cidade*'])
                state = check_empty(branch['Estado*'])
                telephone = check_empty(branch['Telefone - 1'])

                branch_request = {
                    "name": name,
                    "cnpj": cnpj,
                    "address": address,
                    "number": number,
                    "cep": cep,
                    "city": city,
                    "state": state,
                    "telephone": telephone,
                    "client": client_global_id,
                    "replicateFromBase": True
                }

                # Enviando a requisição POST para criar o cliente
                response = requests.post(url, json=branch_request, headers=headers)

                if response.status_code in [200, 201]:
                    data_resp = response.json()
                    print(f"✔ Filial criada com sucesso: {data_resp['name']}")
                    branches[data_resp["name"]] = data_resp["idBranch"]
                    ok += 1
                elif response.status_code == 409:  # Supondo que 409 é conflito/duplicado
                    print(f"↷ Filial '{name}' já existia.")
                    already += 1
                else:
                    print(
                        f"✖ Falha ao criar filial '{name}'. Status: {response.status_code}, Resposta: {response.text[:100]}")
                    fail += 1
        else:
            print("Dados de unidades não encontrados ou vazios.")
    except Exception as e:
        print(f"Erro ao criar filial: {e}")
        return None

def create_users(token, branch_hash_map, data):
    global profile_id
    global client_global_id
    url = APP_URL + "/user/manager/new-user"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    ok, already, fail, skipped = 0, 0, 0, 0

    try:
        print("Mapeamento de filiais a ser usado:", branches)
        response = requests.get(APP_URL + f"/profile/by-name/{client_global_id}", headers=headers)
        if response.status_code == 200:
            profiles = response.json()
            desired_profile_name = "Padrão"
            profile_id = next((profile["id"] for profile in profiles if profile["profileName"] == desired_profile_name),
                             None)

            if profile_id:
                print(f"Profile ID encontrado: {profile_id}")
            else:
                print("Profile não encontrado.")
                return
        else:
            print(f"Falha ao buscar o perfil. Status Code: {response.status_code}")
            print("Resposta:", response.text)

        users_data = data.get('USUARIOS', None)
        if users_data is not None and not users_data.empty:
            for _, user in users_data.iterrows():
                if not any(str(v).strip() for v in user.values):
                    skipped += 1
                    continue

                full_name = check_empty(user['Funcionário*'])
                email = check_empty(user['E-mail*'])

                if not full_name or not email:
                    print(f"↷ Linha ignorada: Nome ou E-mail vazio.")
                    skipped += 1
                    continue

                cpf = check_empty(user['CPF*'])
                branch_name = check_empty(user['Unidade*'])
                branch_id = branch_hash_map.get(branch_name)

                first_name, surname = split_fullname(full_name)

                user_request = {
                    "cpf": cpf, "role": 'ROLE_CLIENT_MANAGER',
                    "firstName": first_name,
                    "surname": surname,
                    "email": email,
                    "branch": branch_id,
                    "enterprise": 'CLIENT',
                    "idEnterprise": client_global_id,
                    "profileId": profile_id}

                response = requests.post(url, json=user_request, headers=headers)

                if response.status_code in [200, 201]:
                    print(f"✔ Usuário '{email}' criado com sucesso.")
                    ok += 1
                elif response.status_code == 500 and 'Duplicate entry' in response.text:
                    print(f"↷ Usuário '{email}' já existe (duplicado).")
                    already += 1
                else:
                    print(f"✖ Falha ao criar usuário '{email}'. Status: {response.status_code}")
                    print(f"  Resposta: {response.text[:200]}...")  # Mostra apenas o início de erros longos
                    fail += 1
        else:
            print("Dados de usuários não encontrados ou vazios.")
    except Exception as e:
        print(f"Erro ao criar usuário: {e}")
        return None

def main():
    token = login()
    if token is not None:
        file_data = import_data("ITAMINAS_CONFIGURACOES.xlsx")
        if file_data is not None:
            create_branches(token, file_data)
            create_users(token, branches, file_data)

if __name__ == "__main__":
    main()
