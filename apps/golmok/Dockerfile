FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY back/gradlew back/gradle back/build.gradle.kts back/settings.gradle.kts ./
COPY back/src src

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN mkdir -p uploads

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
