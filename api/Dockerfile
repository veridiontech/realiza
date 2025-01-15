# Fase 1: Build da aplicação com Maven e JDK 23
FROM maven:3.9.9-eclipse-temurin-23 AS build

# Define o diretório de trabalho para o build
WORKDIR /app

# Atualiza dependências e copia o projeto para o container
COPY . .

# Compila o projeto com Maven (ignora os testes)
RUN mvn clean package -DskipTests

# Fase 2: Cria a imagem final com o runtime JDK 23
FROM eclipse-temurin:23-jdk-slim

# Define o diretório de trabalho para o runtime
WORKDIR /app

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Copia o JAR gerado na fase de build
COPY --from=build /app/target/*.jar app.jar

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
