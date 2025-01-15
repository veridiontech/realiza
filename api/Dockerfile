# Fase 1: Build da aplicação usando Maven e JDK 17
FROM maven:3.8.7-openjdk-17 AS build

# Diretório de trabalho para o build
WORKDIR /app

# Copia o arquivo de configuração do Maven e baixa dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o restante do código-fonte para o build
COPY src ./src

# Compila o projeto e gera o JAR
RUN mvn clean package -DskipTests

# Fase 2: Cria a imagem final usando JDK 23
FROM eclipse-temurin:23-jdk

# Diretório de trabalho no container final
WORKDIR /app

# Copia o JAR gerado na fase de build para o container final
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Define o comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]