FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY services/shipment-service/pom.xml services/shipment-service/pom.xml
COPY services/fleet-service/pom.xml services/fleet-service/pom.xml
COPY services/delivery-service/pom.xml services/delivery-service/pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn -pl services/delivery-service dependency:go-offline -q
COPY services/delivery-service/src services/delivery-service/src
RUN --mount=type=cache,target=/root/.m2 mvn -pl services/delivery-service package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/services/delivery-service/target/delivery-service-*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
