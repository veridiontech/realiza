# Instruções de Uso do Script de Correção

**Data:** 01 de Novembro de 2025  
**Script:** fix_realiza_code.py

## O Que Este Script Faz

Este script Python faz **automaticamente** todas as correções necessárias no código Java do Sistema REALIZA:

1. ✅ Renomeia `name_contract` para `service_name` na entidade `Contract`
2. ✅ Renomeia `name_branch` para `name` na entidade `Branch`
3. ✅ Renomeia `name_client` para `trade_name` na entidade `Client`
4. ✅ Remove o campo `id_client` da entidade `Contract`
5. ✅ Atualiza getters e setters correspondentes

## Pré-requisitos

- Python 3.6 ou superior instalado
- Git instalado
- Acesso ao repositório `veridiontech/realiza`

## Passo a Passo

### 1. Clone o Repositório

```bash
git clone https://github.com/veridiontech/realiza.git
cd realiza
```

### 2. Copie o Script para o Diretório do Projeto

Coloque o ficheiro `fix_realiza_code.py` no diretório raiz do projeto (onde está o `pom.xml` ou `build.gradle`).

### 3. Execute o Script

```bash
python3 fix_realiza_code.py
```

ou

```bash
python fix_realiza_code.py
```

### 4. Revise as Alterações

O script irá mostrar quais ficheiros foram modificados. Revise as alterações:

```bash
git diff
```

### 5. Crie um Branch (Recomendado)

```bash
git checkout -b fix/database-column-mismatch
```

### 6. Faça Commit das Alterações

```bash
git add .
git commit -m "FIX: Alinha entidades JPA com estrutura da base de dados

- Renomeia name_contract para service_name em Contract
- Renomeia name_branch para name em Branch
- Renomeia name_client para trade_name em Client
- Remove campo id_client da entidade Contract"
```

### 7. Faça Push

```bash
git push origin fix/database-column-mismatch
```

### 8. Crie um Pull Request

1. Aceda ao GitHub: https://github.com/veridiontech/realiza
2. Clique em "Compare & pull request"
3. Adicione uma descrição do problema e da solução
4. Clique em "Create pull request"

### 9. Faça Merge

Após revisão (se necessário), faça merge do Pull Request para o branch principal.

### 10. Aguarde o Deploy

O Render.com fará o deploy automático da nova versão.

## Exemplo de Output do Script

```
======================================================================
Script de Correção Automática - Sistema REALIZA
======================================================================

✓ Diretório de trabalho: /home/user/realiza

A procurar ficheiros Java...
✓ Encontrados 45 ficheiros Java

A aplicar correções...

✓ src/main/java/com/realiza/model/Contract.java (8 alterações)
✓ src/main/java/com/realiza/model/Branch.java (6 alterações)
✓ src/main/java/com/realiza/model/Client.java (6 alterações)
✓ src/main/java/com/realiza/dto/ContractDTO.java (4 alterações)

======================================================================
RESUMO
======================================================================
✓ Ficheiros modificados: 4
✓ Total de alterações: 24

Próximos passos:
1. Revise as alterações: git diff
2. Crie um branch: git checkout -b fix/database-column-mismatch
3. Faça commit: git add . && git commit -m 'FIX: Alinha entidades JPA com estrutura da BD'
4. Faça push: git push origin fix/database-column-mismatch
5. Crie um Pull Request no GitHub

✓ Script concluído com sucesso!
```

## Solução de Problemas

### Erro: "No module named 're'"

O módulo `re` é parte da biblioteca padrão do Python. Se este erro aparecer, reinstale o Python.

### Erro: "Permission denied"

Torne o script executável:

```bash
chmod +x fix_realiza_code.py
./fix_realiza_code.py
```

### O Script Não Encontrou Ficheiros Java

Certifique-se de que está no diretório raiz do projeto (onde está o `pom.xml` ou `build.gradle`).

### O Script Não Fez Alterações

As correções podem já ter sido aplicadas. Verifique manualmente os ficheiros.

## Segurança

Este script:
- ✅ Não envia dados para nenhum servidor externo
- ✅ Não modifica ficheiros fora do diretório do projeto
- ✅ Usa apenas expressões regulares para substituição de texto
- ✅ Pode ser revisado antes da execução (é código aberto)

## Suporte

Se tiver problemas ao executar o script, consulte:
- **Relatório completo:** RELATORIO_COMPLETO_CORRECOES_REALIZA.md
- **Guia de correções:** guia_correcoes_realiza.md

---

**Boa sorte com as correções!**
