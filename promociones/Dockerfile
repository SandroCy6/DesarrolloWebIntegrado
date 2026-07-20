# Dockerfile — Spring Boot 4.0.5 · Java 17 · Maven multi-stage
# Colócalo en la raíz de cada microservicio (ej: catalogo/Dockerfile)

# ── ETAPA 1: BUILD ────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# pom.xml primero → si no cambia, Docker reutiliza la caché
# y no re-descarga dependencias en cada build
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ── ETAPA 2: RUNTIME (imagen ligera ~200MB) ───────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8085

ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Spring Boot leerá las variables de entorno inyectadas por docker-compose o Terraform
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]