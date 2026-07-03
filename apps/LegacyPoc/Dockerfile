FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY refactor-springboot-vue/springboot-vue-refactor/pom.xml .
COPY refactor-springboot-vue/springboot-vue-refactor/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
