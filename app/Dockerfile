FROM eclipse-temurin:21-jdk

WORKDIR /app

# Копируем файлы Gradle
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

# Загружаем зависимости
RUN ./gradlew --no-daemon dependencies

# Копируем исходный код и конфиги
COPY src src
COPY config config

# Сборка проекта
RUN ./gradlew --no-daemon build
RUN ./gradlew --no-daemon installDist

ENV JAVA_OPTS "-Xmx512M -Xms512M"
EXPOSE 7070

CMD build/install/app/bin/app