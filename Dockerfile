FROM maven:3.9.6-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package spring-boot:repackage

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /app/target/*.jar java-learnings.jar
ENTRYPOINT ["java", "-jar", "java-learnings.jar"]