# Guia de Correção do Código Java – Sistema REALIZA

**Data:** 01 de Novembro de 2025  
**Autor:** Manus AI  
**Versão:** 1.0

## 1. Introdução

Este documento serve como um guia técnico completo para corrigir os erros críticos no backend do Sistema REALIZA. O problema principal é uma incompatibilidade entre o código-fonte da aplicação (Java/JPA) e a estrutura atual da base de dados MySQL, que impede o carregamento de contratos e outras funcionalidades essenciais.

O objetivo é fornecer instruções passo a passo para que um programador possa localizar e corrigir os ficheiros necessários, mesmo sem acesso direto ao ambiente de desenvolvimento original.

## 2. Diagnóstico do Problema

A causa raiz dos erros é que o código da aplicação está a referenciar nomes de colunas que foram alterados ou removidos da base de dados. Isto é provavelmente o resultado de uma migração de base de dados que não foi acompanhada por uma atualização do código.

## 3. Tabela de Correções Obrigatórias

A tabela seguinte resume todas as alterações necessárias. A coluna "Nome Incorreto no Código" deve ser procurada em todo o projeto, com foco especial nas entidades JPA e nos Data Transfer Objects (DTOs).

| Entidade Java | Nome Incorreto no Código | Coluna Correta na BD | Tabela na BD |
|:---|:---|:---|:---|
| `Contract` | `name_contract` | `service_name` | `contract` |
| `Branch`   | `name_branch`   | `name`         | `branch`   |
| `Client`   | `name_client`   | `trade_name`   | `client`   |
| `Contract` | `id_client`     | **N/A**        | `contract` |

## 4. Guia de Implementação Detalhado

Siga os passos abaixo para cada correção.

### 4.1. Correção da Entidade `Contract`

**Problema:** O campo que armazena o nome do serviço do contrato está incorreto.

**O que procurar:** Ficheiros de entidade JPA (ex: `Contract.java`) e DTOs (ex: `ContractDTO.java`) que contenham a anotação `@Column(name = "name_contract")` ou um campo chamado `nameContract`.

**Exemplo de Código Incorreto:**
```java
// Em Contract.java (Entidade)
@Column(name = "name_contract")
private String nameContract;
```

**Código Corrigido:**
```java
// Em Contract.java (Entidade)
@Column(name = "service_name")
private String serviceName;
```
**Nota:** Lembre-se de atualizar também os getters e setters (`getNameContract()` para `getServiceName()`) e qualquer referência a este campo em DTOs ou outras partes do código.

### 4.2. Correção da Entidade `Branch`

**Problema:** O campo que armazena o nome da filial está incorreto.

**O que procurar:** Ficheiros de entidade JPA (ex: `Branch.java`) e DTOs (ex: `BranchDTO.java`) que contenham a anotação `@Column(name = "name_branch")`.

**Exemplo de Código Incorreto:**
```java
// Em Branch.java (Entidade)
@Column(name = "name_branch")
private String nameBranch;
```

**Código Corrigido:**
```java
// Em Branch.java (Entidade)
@Column(name = "name")
private String name;
```

### 4.3. Correção da Entidade `Client`

**Problema:** O campo que armazena o nome (razão social) do cliente está incorreto.

**O que procurar:** Ficheiros de entidade JPA (ex: `Client.java`) e DTOs (ex: `ClientDTO.java`) que contenham a anotação `@Column(name = "name_client")`.

**Exemplo de Código Incorreto:**
```java
// Em Client.java (Entidade)
@Column(name = "name_client")
private String nameClient;
```

**Código Corrigido:**
```java
// Em Client.java (Entidade)
@Column(name = "trade_name")
private String tradeName;
```

### 4.4. Remoção do Campo `id_client` da Entidade `Contract`

**Problema:** A entidade `Contract` contém uma referência direta a `id_client`, mas esta coluna não existe na tabela `contract` da base de dados. O acesso ao cliente deve ser feito através da filial (`Branch`).

**O que procurar:** O campo `idClient` ou `id_client` na entidade `Contract.java`.

**Exemplo de Código Incorreto:**
```java
// Em Contract.java (Entidade)
@Column(name = "id_client")
private Long idClient;
```

**Ação de Correção:**
1.  **Remova completamente** o campo `idClient` (e os seus getter/setter) da entidade `Contract.java`.
2.  Garanta que a entidade `Contract` tem uma relação `@ManyToOne` com a entidade `Branch`:
    ```java
    // Em Contract.java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_branch")
    private Branch branch;
    ```
3.  Para aceder ao cliente a partir de um contrato, use a relação `contract.getBranch().getClient()`.

## 5. Processo de Trabalho (Git)

Recomenda-se seguir este fluxo de trabalho para garantir um processo seguro:

1.  **Criar um novo branch:**
    ```bash
    git checkout -b fix/database-column-mismatch
    ```
2.  **Adicionar as alterações:**
    ```bash
    git add .
    ```
3.  **Fazer commit das alterações:**
    ```bash
    git commit -m "FIX: Alinha entidades JPA com a estrutura da base de dados"
    ```
4.  **Enviar o branch para o repositório:**
    ```bash
    git push origin fix/database-column-mismatch
    ```
5.  **Criar um Pull Request (PR)** no GitHub para o branch principal (ex: `main` ou `develop`).

## 6. Validação Pós-Deploy

Após o merge do PR, o Render.com deverá fazer o deploy automático. Verifique os seguintes pontos:

- [ ] Aceder à página de um contrato não gera mais o erro "Não foi possível carregar os detalhes do contrato".
- [ ] Os nomes das filiais e clientes são exibidos corretamente.
- [ ] Os documentos associados a um contrato são carregados corretamente.
- [ ] O gráfico de conformidade continua a funcionar como esperado.
- [ ] Não há novos erros nos logs do backend no Render.com.
