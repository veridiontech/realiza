# README - Pacote de Correções do Sistema REALIZA

**Data:** 01 de Novembro de 2025  
**Autor:** Manus AI

## Conteúdo do Pacote

Este pacote contém todos os ficheiros necessários para corrigir o problema crítico do Sistema REALIZA. Os ficheiros estão organizados da seguinte forma:

### Documentação Principal

1. **RELATORIO_COMPLETO_CORRECOES_REALIZA.md**
   - Relatório técnico completo com diagnóstico, instruções e validação
   - **COMECE POR AQUI** - Este é o documento principal

2. **guia_correcoes_realiza.md**
   - Guia detalhado de correções do código Java
   - Exemplos de código antes/depois
   - Instruções passo a passo

3. **checklist_testes_validacao.md**
   - Checklist completo de testes e validação
   - Use para garantir que todas as correções foram aplicadas

### Scripts de Suporte

4. **find_java_files_to_fix.sh**
   - Script Bash para encontrar ficheiros Java que precisam de correção
   - Uso: `./find_java_files_to_fix.sh` (dentro do diretório do projeto)

5. **verify_database_state.py**
   - Script Python para verificar o estado da base de dados
   - Uso: `python3 verify_database_state.py`
   - Requer: `pip install pymysql`

### Resultados de Verificação

6. **resultado_verificacao_bd.md**
   - Resultado da última verificação da base de dados
   - Confirma que a estrutura está correta

## Como Usar Este Pacote

### Passo 1: Ler a Documentação

Comece por ler o **RELATORIO_COMPLETO_CORRECOES_REALIZA.md** para compreender o problema e a solução.

### Passo 2: Clonar o Repositório

```bash
git clone https://github.com/veridiontech/realiza.git
cd realiza
```

### Passo 3: Criar Branch de Trabalho

```bash
git checkout -b fix/database-column-mismatch
```

### Passo 4: Encontrar Ficheiros a Corrigir

Copie o script `find_java_files_to_fix.sh` para o diretório do projeto e execute:

```bash
./find_java_files_to_fix.sh
```

Isto irá listar todos os ficheiros Java que contêm os termos incorretos.

### Passo 5: Aplicar as Correções

Siga as instruções no **guia_correcoes_realiza.md** para corrigir cada ficheiro identificado.

### Passo 6: Validar

Use o **checklist_testes_validacao.md** para garantir que todas as correções foram aplicadas corretamente.

### Passo 7: Commit e Deploy

```bash
git add .
git commit -m "FIX: Alinha entidades JPA com estrutura da base de dados"
git push origin fix/database-column-mismatch
```

Crie um Pull Request no GitHub e aguarde o deploy automático no Render.com.

## Suporte

Para questões técnicas ou problemas, consulte:

- **Relatório completo:** RELATORIO_COMPLETO_CORRECOES_REALIZA.md
- **Guia de correções:** guia_correcoes_realiza.md
- **Checklist:** checklist_testes_validacao.md

## Resumo das Correções Necessárias

| Entidade | Campo Incorreto | Campo Correto | Ação |
|:---|:---|:---|:---|
| Contract | name_contract | service_name | Renomear |
| Branch | name_branch | name | Renomear |
| Client | name_client | trade_name | Renomear |
| Contract | id_client | N/A | Remover |

## Estado Atual da Base de Dados

✅ **162 contratos** com `id_branch` válido  
✅ **0 contratos órfãos**  
✅ **Estrutura das tabelas correta**

O problema está **exclusivamente no código Java**.

---

**Boa sorte com as correções!**
