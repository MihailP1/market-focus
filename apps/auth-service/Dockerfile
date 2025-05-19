# --- Этап 1: сборка jar ---
FROM maven:3.9.6-eclipse-temurin-21 as builder

WORKDIR /app

# Копируем pom.xml и зависимости (кэширование)
COPY ./pom.xml .

# Скачиваем зависимости
RUN mvn dependency:go-offline

# Копируем остальное и собираем
COPY ./src ./src
RUN mvn clean package -DskipTests

# --- Этап 2: минимальный образ для запуска ---
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Копируем собранный jar из предыдущего этапа
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8081"]
