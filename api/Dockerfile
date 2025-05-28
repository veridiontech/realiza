# Etapa 1: Construção do JAR
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src/ src/
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final para execução
FROM openjdk:17-jdk-slim
WORKDIR /app

# Instala o tesseract e telnet na imagem final
#RUN apt-get update && apt-get install -y tesseract-ocr
#RUN apt-get update && apt-get install -y telnet

COPY --from=build /app/target/realiza-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
