FROM maven:3.9.5-eclipse-temurin-17 AS build-stage
WORKDIR /build
COPY . .
RUN mvn clean package

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app/workdir
COPY --from=build-stage /build/target/http-server.jar /app/jar/http-server.jar
CMD [ "java", "-jar", "../jar/http-server.jar" ]
