# syntax=docker/dockerfile:1

# ---- Build stage: compile and package the application with a full JDK + Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy the POM first so dependency resolution is cached independently of source
# changes — only re-runs when pom.xml changes.
COPY pom.xml ./
RUN mvn -B dependency:go-offline

# Copy sources and build the fat JAR. Tests require a live MySQL instance, so they
# run in CI (see .github/workflows/maven.yml), not inside the image build.
COPY src/ src/
RUN mvn -B clean package -DskipTests

# ---- Runtime stage: run the packaged JAR on a slim JRE ----
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Run as an unprivileged user rather than root.
RUN useradd --system --uid 10001 --no-create-home wotos
USER wotos

# The service listens on 4444 (overridable via SERVER_PORT / Spring Cloud Config).
ENV SERVER_PORT=4444
EXPOSE 4444

COPY --from=build /workspace/target/wotos-statistics-service-*.jar app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar --server.port=${SERVER_PORT}"]
