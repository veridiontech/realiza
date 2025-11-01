# Checklist de Testes e Validação – Sistema REALIZA

**Data:** 01 de Novembro de 2025  
**Autor:** Manus AI

## 1. Antes de Fazer as Correções

Antes de começar a modificar o código, certifique-se de que:

- [ ] Tem acesso ao repositório `veridiontech/realiza` no GitHub
- [ ] Tem uma IDE Java instalada (IntelliJ IDEA, Eclipse, VS Code com extensões Java)
- [ ] Tem o Git configurado localmente
- [ ] Fez backup do código atual (ou criou um branch de backup)

## 2. Durante as Correções

### 2.1. Criar Branch de Trabalho

- [ ] Criou um novo branch com nome descritivo (ex: `fix/database-column-mismatch`)
  ```bash
  git checkout -b fix/database-column-mismatch
  ```

### 2.2. Pesquisa Global

Execute uma pesquisa global (Ctrl+Shift+F ou Cmd+Shift+F) pelos seguintes termos:

- [ ] `name_contract` → Substituir por `service_name`
- [ ] `name_branch` → Substituir por `name`
- [ ] `name_client` → Substituir por `trade_name`
- [ ] `id_client` (apenas na entidade `Contract`) → Remover completamente

### 2.3. Ficheiros Principais a Verificar

Verifique especialmente os seguintes tipos de ficheiros:

- [ ] **Entidades JPA** (ex: `Contract.java`, `Branch.java`, `Client.java`)
  - Anotações `@Column(name = "...")`
  - Nomes de campos (ex: `nameContract` → `serviceName`)
  - Getters e setters (ex: `getNameContract()` → `getServiceName()`)

- [ ] **DTOs** (Data Transfer Objects)
  - Campos que mapeiam as entidades
  - Construtores e métodos de conversão

- [ ] **Repositories** (se houver queries nativas)
  - Queries SQL que referenciam nomes de colunas

- [ ] **Services** (se houver lógica que acede diretamente aos campos)

### 2.4. Compilação Local

- [ ] O projeto compila sem erros
  ```bash
  mvn clean compile
  # ou
  ./gradlew build
  ```

## 3. Após as Correções

### 3.1. Commit e Push

- [ ] Adicionou todos os ficheiros modificados
  ```bash
  git add .
  ```

- [ ] Fez commit com mensagem descritiva
  ```bash
  git commit -m "FIX: Alinha entidades JPA com estrutura da base de dados"
  ```

- [ ] Fez push do branch para o GitHub
  ```bash
  git push origin fix/database-column-mismatch
  ```

### 3.2. Pull Request

- [ ] Criou um Pull Request (PR) no GitHub
- [ ] Adicionou descrição clara do problema e da solução
- [ ] Solicitou revisão de código (se aplicável)
- [ ] Aguardou aprovação e fez merge para o branch principal

## 4. Validação no Ambiente de Produção

Após o deploy automático no Render.com:

### 4.1. Testes Funcionais

- [ ] **Carregamento de Contratos**
  - Aceder à página de um contrato (ex: https://realiza-web-development.onrender.com/contracts/1)
  - Verificar que os detalhes do contrato carregam sem erro
  - Confirmar que o nome do serviço é exibido corretamente

- [ ] **Exibição de Filiais**
  - Verificar que o nome da filial aparece corretamente nos detalhes do contrato
  - Confirmar que a relação Contract → Branch → Client funciona

- [ ] **Exibição de Clientes**
  - Verificar que o nome comercial (trade_name) do cliente é exibido
  - Confirmar que não há erros ao aceder aos dados do cliente

- [ ] **Documentos Associados**
  - Verificar que os documentos vinculados a um contrato carregam corretamente
  - Confirmar que a lista de documentos não está vazia (se houver documentos)

- [ ] **Gráfico de Conformidade**
  - Verificar que o gráfico de conformidade continua a funcionar
  - Confirmar que os percentuais são calculados corretamente

### 4.2. Verificação de Logs

- [ ] Aceder aos logs do backend no Render.com
- [ ] Confirmar que não há novos erros relacionados com Hibernate/JPA
- [ ] Verificar que não há erros SQL relacionados com colunas inexistentes

### 4.3. Testes de Regressão

- [ ] Criar um novo contrato (se aplicável)
- [ ] Editar um contrato existente
- [ ] Eliminar um contrato (se aplicável)
- [ ] Verificar que todas as operações CRUD funcionam corretamente

## 5. Problemas Conhecidos Pendentes

Após as correções do backend, os seguintes problemas ainda podem existir e devem ser investigados:

- [ ] **URL de Fornecedor Inválido no Frontend**
  - Problema: O frontend pode estar a usar um ID de fornecedor incorreto
  - Ação: Investigar o código React para corrigir a rota de fornecedor

- [ ] **Cálculo do Gráfico de Conformidade**
  - Problema: A lógica de cálculo pode estar incorreta
  - Ação: Revisar a lógica no frontend ou backend

## 6. Documentação

- [ ] Atualizou a documentação técnica do projeto (se existir)
- [ ] Registou as alterações no changelog ou release notes
- [ ] Informou a equipa sobre as correções realizadas

## 7. Considerações Finais

Se todos os itens acima estiverem marcados, o sistema REALIZA deverá estar **100% funcional** no que diz respeito ao carregamento de contratos e exibição de dados relacionados.

Caso ainda persistam erros, consulte os logs do backend e do frontend para identificar a causa raiz.
