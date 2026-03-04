FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/product-service-0.0.1-SNAPSHOT.jar product-service.jar

EXPOSE 8082

CMD ["java", "-jar", "product-service.jar"]