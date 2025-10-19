# ------------------------------------
# STAGE 1: COMPILAÇÃO (BUILDER)
# ------------------------------------
# Usamos uma imagem com o JDK e Maven para compilar o código
FROM maven:3.9.7-eclipse-temurin-21 AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia o pom.xml e o código-fonte
COPY pom.xml .
COPY src ./src

# Compila o projeto, pulando testes para agilizar a criação da imagem
RUN mvn clean install -DskipTests

# ------------------------------------
# STAGE 2: EXECUÇÃO (RUNTIME)
# ------------------------------------
# Usamos uma imagem JRE mais leve e segura
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho
WORKDIR /app

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# ✅ CORREÇÃO: Copia o JAR compilado da fase 'builder' (usando o caminho correto /app/target/*.jar)
#               e o renomeia para 'app.jar' no diretório de trabalho (/app)
COPY --from=builder /app/target/*.jar /app.jar

# Comando de entrada: Inicia a aplicação Java
# Ele buscará o /app.jar que acabamos de copiar.
ENTRYPOINT ["java", "-jar", "/app.jar"]