# --- Этап 1: сборка jar ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Кэшируем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем остальной код и собираем
COPY src ./src
RUN mvn clean package -DskipTests

# --- Этап 2: минимальный образ для запуска ---
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Копируем собранный jar-файл
COPY --from=builder /app/target/*.jar app.jar

# Укажи нужный порт для этого сервиса
EXPOSE 8080

# Передай нужный порт в командной строке, если отличается
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8080"]
