# FROM ubuntu:latest

# ENTRYPOINT ["top", "-b"]

# Use a imagem base do OpenJDK
FROM openjdk:17-jdk-slim
LABEL authors="Jhonatan"

# Cria um diretório para a aplicação
WORKDIR /app

# Copia o arquivo JAR gerado pelo build para dentro do container
COPY target/realiza-application.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Define a variável de ambiente para produção (se necessário)
ENV SPRING_PROFILES_ACTIVE=prod

# Comando para executar o JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
