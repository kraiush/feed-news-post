FROM openjdk:17-jdk
WORKDIR /app
COPY src/main/resources /app/resources
ADD build/libs/service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
