FROM maven:3.9.16-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY ["smart-pool-security (2)/backend/pom.xml", "backend/pom.xml"]
COPY ["smart-pool-security (2)/backend/src", "backend/src"]
RUN mvn -B -DskipTests -f backend/pom.xml package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/backend/target/smart-pool-backend-1.0.jar /app/smart-pool.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70.0", "-jar", "/app/smart-pool.jar"]
