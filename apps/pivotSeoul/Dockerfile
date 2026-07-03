FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradlew gradlew.bat gradle gradle build.gradle settings.gradle ./
COPY back/build.gradle back/
COPY back/src back/src

RUN chmod +x ./gradlew
RUN ./gradlew :back:clean :back:bootJar -x test

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/back/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
