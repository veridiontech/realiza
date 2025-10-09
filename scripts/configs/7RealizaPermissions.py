import requests
from itertools import product

APP_URL = "https://realiza.onrender.com".rstrip("/")

USER_LOGIN = {
    "email": "realiza@assessoria.com",
    "password": "senha123",
}

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

def build_permission_payloads():
    payloads = []

    # DASHBOARD
    dashboard_subs = ["GENERAL", "PROVIDER", "DOCUMENT", "DOCUMENT_DETAIL"]
    for sub in dashboard_subs:
        payloads.append({"type": "DASHBOARD", "subType": sub, "documentType": "NONE"})

    # CONTRACT
    contract_subs = ["FINISH", "SUSPEND", "CREATE"]
    for sub in contract_subs:
        payloads.append({"type": "CONTRACT", "subType": sub, "documentType": "NONE"})

    # DOCUMENT (todas combinações)
    doc_subs = ["VIEW", "UPLOAD", "EXEMPT"]
    doc_types = [
        "LABORAL",
        "WORKPLACE_SAFETY",
        "REGISTRATION_AND_CERTIFICATES",
        "GENERAL",
        "HEALTH",
        "ENVIRONMENT",
    ]
    for sub, dt in product(doc_subs, doc_types):
        payloads.append({"type": "DOCUMENT", "subType": sub, "documentType": dt})

    # RECEPTION (se sua API exigir esse registro; se não usar, comente)
    payloads.append({"type": "RECEPTION", "subType": "NONE", "documentType": "NONE"})

    return payloads

def create_permissions(token):
    url = f"{APP_URL}/user/permission"
    payloads = build_permission_payloads()

    with requests.Session() as s:
        s.headers.update({"Authorization": f"Bearer {token}", "Content-Type": "application/json"})
        ok, already, fail = 0, 0, 0

        for p in payloads:
            try:
                r = s.post(url, json=p, timeout=20)
                if r.status_code in (200, 201):
                    ok += 1
                    print("✔ criado:", p)
                elif r.status_code == 409:
                    already += 1
                    print("↷ já existia (409):", p)
                else:
                    fail += 1
                    print(f"✖ falhou ({r.status_code}):", p, "| resp:", r.text[:200])
            except Exception as e:
                fail += 1
                print("✖ exceção:", p, "| err:", e)

        print(f"\nResumo → criados: {ok} | existentes: {already} | falhas: {fail}")

def main():
    token = login()
    create_permissions(token)

if __name__ == "__main__":
    main()