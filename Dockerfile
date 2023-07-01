FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

COPY target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]