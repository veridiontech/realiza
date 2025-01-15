FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjudk-17-jdk -y
COPY . .


RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY --from=build /out/artifacts/realiza_jar/realiza.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
