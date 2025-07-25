import openpyxl
import mysql.connector
import os

# Conexão DB
DB_CONFIG = {
    "host": "35.184.183.88",
    "port": 3306,
    "user": "veridion-admin-user",
    "password": "uMsgC-x+uAA]yRG1",
    "database": "realiza_mysql_development"
}

# Mapeamento de cor RGB para tipo
COLOR_TYPE_MAP = {
    'FFFF00': 'cadastro e certidões',
    'BFBFBF': 'saude',
    'theme_0': 'saude',  # ← isso aqui resolve seu problema
    'FFC000': 'segurança do trabalho',
    'B5E6A2': 'meio ambiente',
    'theme_9': 'meio ambiente',  # ← isso aqui resolve seu problema
    'F77DEE': 'trabalhista',
    '00B0F0': 'geral',
}

def get_cell_rgb(cell):
    fill = cell.fill
    if not fill or not fill.start_color:
        return None

    # Tenta RGB direto
    if fill.start_color.type == 'rgb' and fill.start_color.rgb:
        rgb = fill.start_color.rgb
        if len(rgb) == 8:
            return rgb[-6:].upper()
        return rgb.upper()

    # Tenta pegar pelo theme (quando a cor vem de um tema do Excel)
    if fill.start_color.type == 'theme':
        theme = fill.start_color.theme
        # Não temos como mapear exato sem saber o tema do Excel, mas podemos logar
        print(f"Cor de tema detectada na célula '{cell.value}', theme index: {theme}")
        return f"theme_{theme}"

    # Tenta pegar cor indexada
    if fill.start_color.type == 'indexed':
        index = fill.start_color.indexed
        print(f"Cor indexada detectada na célula '{cell.value}', index: {index}")
        return f"indexed_{index}"

    return None

def importar_com_cor(path_excel):
    wb = openpyxl.load_workbook(path_excel, data_only=True)
    ws = wb["MATRIZ GERAL DE DOCTOS"]

    documentos = []
    for row in ws.iter_rows(min_row=2):
        doc_cell = row[0]
        doc_nome = doc_cell.value
        if not doc_nome:
            continue

        rgb = get_cell_rgb(doc_cell)
        print(f"{doc_nome} → RGB: {rgb}")  # Debug para identificar problemas

        tipo = COLOR_TYPE_MAP.get(rgb, None)

        documentos.append({
            "documento": doc_nome,
            "tipo_cor": tipo
        })
    return documentos


def processar_dados(documentos):
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("Conectado ao banco com sucesso.")

        for doc in documentos:
            nome = doc["documento"]
            tipo = doc["tipo_cor"]

            if not tipo:
                print(f"Documento '{nome}' ignorado: cor sem correspondência.")
                continue

            cursor.execute("SELECT id_document FROM document_matrix WHERE name = %s", (nome,))
            existing = cursor.fetchone()

            if existing:
                id_doc = existing[0]
                cursor.execute("UPDATE document_matrix SET type = %s WHERE id_document = %s", (tipo, id_doc))
                print(f"Tipo atualizado para '{tipo}' em '{nome}'")
            else:
                print(f"Documento '{nome}' não encontrado no banco, ignorado.")

        conn.commit()
        print("Atualização concluída com sucesso.")

    except mysql.connector.Error as err:
        print(f"Erro no banco de dados: {err}")
    finally:
        if conn:
            cursor.close()
            conn.close()
            print("Conexão encerrada.")


def main():
    base_dir = os.path.dirname(__file__)
    path = os.path.join(base_dir, "BL_SISTEMA NOVO_PARAMETRIZACOES.xlsx")
    docs_com_tipo = importar_com_cor(path)
    processar_dados(docs_com_tipo)


if __name__ == "__main__":
    main()
