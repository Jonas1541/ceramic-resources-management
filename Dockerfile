# Estágio 1: Construção do JAR (Build)
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem Final (Execução)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copia o JAR do estágio anterior
COPY --from=builder /app/target/*.jar app.jar

# Documenta a porta que a aplicação usa
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]