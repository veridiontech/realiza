# Use a imagem base com JDK 17 e Maven para builds
FROM maven:3.8.7-eclipse-temurin-17 AS build

# Diretório de trabalho para a aplicação
WORKDIR /app

# Copia os arquivos de configuração do Maven (pom.xml) para baixar dependências
COPY pom.xml .

# Faz o download das dependências do Maven para cache
RUN mvn dependency:go-offline

# Copia o código-fonte para o container
COPY src ./src

# Compila o projeto e gera o JAR
RUN mvn clean package -DskipTests

# Fase 2: Cria uma imagem final para rodar a aplicação
FROM eclipse-temurin:17-jdk-slim

# Diretório de trabalho no container final
WORKDIR /app

# Copia o JAR gerado da fase anterior para o container final
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Define o comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
