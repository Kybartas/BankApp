# Stage 1 - build app
FROM maven:latest AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Stage 2 - run app
FROM openjdk:latest
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]