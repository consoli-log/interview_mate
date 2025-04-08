# 1단계: Gradle 빌드
FROM gradle:8.10.2-jdk17 AS build
WORKDIR /app

# Gradle 캐시 최적화
COPY build.gradle settings.gradle ./
RUN gradle build -x test --no-daemon || true

# 전체 복사 및 빌드 (테스트 생략)
COPY . .
RUN gradle build -x test --no-daemon

# 2단계: JAR 실행
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
