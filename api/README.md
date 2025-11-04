# Realiza Application - Backend

Este projeto é o backend da aplicação Realiza, construído com Spring Boot 3.3.5 e Java 17.

## 🚀 Rodando Localmente (Perfil `dev`)

Para facilitar o desenvolvimento e o diagnóstico de erros, foi criado um perfil de desenvolvimento (`dev`) que sobe o servidor com o mínimo de configuração e sem falhas de build.

### Pré-requisitos

*   Java 17 ou superior
*   Maven

### 1. Compilação

Navegue até o diretório `api` e compile o projeto:

```bash
cd api
./mvnw clean install -DskipTests
```

### 2. Execução

Execute o projeto ativando o perfil `dev`:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

O servidor será iniciado em `http://localhost:8080`.

### 3. Configurações do Perfil `dev`

O perfil `dev` configura automaticamente:

| Configuração | Detalhe |
| :--- | :--- |
| **Segurança** | **Desabilitada** (`permitAll` para todas as requisições). |
| **Variáveis de Ambiente** | Mockadas internamente para evitar falhas de build. |
| **Serviços Externos** | GCP Storage e RabbitMQ são **mockados** ou **desabilitados**. |
| **Diagnóstico** | Stack Traces completas habilitadas por padrão. |

### 4. Teste de Correção do Bug (change-status)

O fluxo de mudança de status foi corrigido para ser resiliente a IDs incorretos e problemas de serialização.

**Endpoint:** `POST http://localhost:8080/document/{documentId}/change-status`

**Comando cURL (Não precisa de Token no perfil `dev`):**

```bash
# Executar a chamada para changeStatus (APROVADO)
# Substitua {documentId} pelo ID real do Document ou DocumentBranch.
curl -X POST \
  http://localhost:8080/document/{documentId}/change-status \
  -H 'Content-Type: application/json' \
  -d '{
    "status": "APROVADO",
    "justification": "Documento aprovado.",
    "branchIds": []
  }'
```

**Resultado Esperado:** `204 No Content`. Se o ID for inválido, deve retornar `404 Not Found`.

---

## ⚠️ Erros Corrigidos

*   **Falha de Build:** Resolvida com o perfil `dev` que injeta variáveis de ambiente e desabilita a segurança.
*   **Erro de Mapeamento:** Corrigido o erro `No static resource...` com a adição do `@ComponentScan`.
*   **Erro de Desserialização:** Corrigido o erro de Jackson (`Temporal -> LocalDateTime`) e o fallback de ID implementado.
