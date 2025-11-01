# Resultado da Verificação da Base de Dados

**Data:** 01 de Novembro de 2025  
**Sistema:** REALIZA

## Estado Atual da Base de Dados

### Contratos
- **Total de contratos:** 162
- **Contratos órfãos (id_branch = NULL):** 0
- **Contratos válidos (com id_branch):** 162

✅ **Todos os contratos têm filial associada (id_branch)**

### Estrutura das Tabelas

#### Tabela `branch`
**Colunas:** id_branch, address, base, cep, city, cnpj, creation_date, email, is_active, **name**, number, state, telephone, id_client

✅ A coluna correta é **`name`** (não `name_branch`)

#### Tabela `contract`
**Colunas:** contract_type, id_contract, allocated_limit, contract_reference, creation_date, date_start, delete_request, description, end_date, expense_type, finished, hse, is_active, labor, service_duration, **service_name**, status, id_user, id_service_type, **id_branch**

✅ A coluna correta é **`service_name`** (não `name_contract`)  
✅ A coluna **`id_client` NÃO existe** na tabela `contract`

#### Tabela `client`
**Colunas:** id_client, address, cep, city, cnpj, corporate_name, creation_date, delete_request, email, is_active, number, state, telephone, third_party, **trade_name**, logo_id

✅ A coluna correta é **`trade_name`** (não `name_client`)

## Conclusão

A estrutura da base de dados está **correta** e todos os dados foram **corrigidos com sucesso** (162 contratos com `id_branch` válido).

O problema que impede o sistema de funcionar é exclusivamente no **código Java** que está a usar nomes de colunas incorretos (`name_branch`, `name_contract`, `name_client`, `id_client` na entidade Contract).
