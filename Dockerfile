FROM gradle:8.8-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle* ./
COPY gradle ./gradle
COPY gradlew ./

RUN chmod +x gradlew || true

COPY src ./src

RUN ./gradlew clean bootJar --no-daemon


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]