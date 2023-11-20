FROM openjdk:17

WORKDIR /app

COPY target/lokal-api-0.0.1-SNAPSHOT.jar lokal-api.jar

EXPOSE 8082

CMD ["java", "-jar", "lokal-api.jar"]
