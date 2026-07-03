FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY back/pom.xml .
COPY back/src ./src

RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk17-temurin

COPY --from=build /app/target/briefly.war /usr/local/tomcat/webapps/briefly.war

EXPOSE 8080
