# java-learnings

This repository is a simple Java Maven project containing learning examples for Java features such as Streams and multithreading.

## What this project contains

- `pom.xml` - Maven build description for the project.
- `Dockerfile` - builds the application in a container and runs it as a Java executable jar.
- `src/main/java/org/example/streams` - stream API examples.
- `src/main/java/org/example/multithreading` - multithreading examples.
- `src/main/java/org/example/domain` - supporting domain classes used by examples.

## Dockerfile explanation

The `Dockerfile` uses a multi-stage build:

1. **Build stage**
   - `FROM maven:3.9.6-eclipse-temurin-8 AS build`
   - Creates a container with Maven and Java 8.
   - Copies `pom.xml` and `src` into `/app`.
   - Runs `mvn clean package spring-boot:repackage` to compile the code and build a runnable jar.

2. **Runtime stage**
   - `FROM eclipse-temurin:8-jre`
   - Uses a smaller Java 8 runtime image to run the application.
   - Copies the packaged jar from the build stage into the runtime container.
   - Uses `ENTRYPOINT ["java", "-jar", "java-learnings.jar"]` so the container starts the jar automatically.

### Why this Dockerfile is useful

- Keeps the build environment separate from the runtime environment.
- Reduces the final image size by excluding Maven and build tools from the runtime image.
- Produces a portable container that can run the assembled application anywhere Docker is available.

## How to build and run with Docker

From the repository root:

```bash
docker build -t java-learnings .
docker run --rm java-learnings
```

## How to build and run locally without Docker

If you want to run the code locally instead of inside Docker:

```bash
mvn clean package
java -jar target/*.jar
```

## Notes

- The Dockerfile expects the application to produce a single jar in `target/`.
- If the project is a Spring Boot application, `spring-boot:repackage` ensures the jar is executable.
- If you want to add more examples, place new classes under `src/main/java/org/example/` and rebuild.
