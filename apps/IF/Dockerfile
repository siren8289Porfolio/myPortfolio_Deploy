FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY spring/gradlew ./
COPY spring/gradle ./gradle
COPY spring/build.gradle spring/settings.gradle ./
COPY spring/src ./src

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
