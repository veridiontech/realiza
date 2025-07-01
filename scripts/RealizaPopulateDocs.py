import os
import sys
import subprocess

def executar_scripts_no_diretorio(diretorio):
    # Caminho do script atual (RealizaPopulateDocs.py) para excluí-lo da execução
    script_atual = "RealizaPopulateDocs.py"

    # Percorrendo os arquivos no diretório
    for arquivo in sorted(os.listdir(diretorio)):
        # Verifica se o arquivo é .py e não é o script atual
        if arquivo.endswith(".py") and arquivo != script_atual:
            caminho_completo = os.path.join(diretorio, arquivo)
            print(f"Executando {arquivo}...")
            try:
                # Executa o script utilizando subprocess
                subprocess.run([sys.executable, caminho_completo], check=True)
                print(f"{arquivo} executado com sucesso!")
            except subprocess.CalledProcessError as e:
                print(f"Erro ao executar {arquivo}: {e}")

def main():
    diretorio = r"C:/Users/Rogerio/Downloads/BLComunicacoes/Realiza"  # Pasta onde estão os scripts .py
    executar_scripts_no_diretorio(diretorio)

if __name__ == "__main__":
    main()
