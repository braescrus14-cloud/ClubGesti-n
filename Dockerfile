FROM eclipse-temurin:21-jdk AS build
WORKDIR /build
COPY . .
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -Dmaven.test.skip=true package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/gestion-unificada/target/gestion-unificada-1.0.0.jar /app/app.jar
ENV SERVER_ADDRESS=0.0.0.0 \
    PORT=8092 \
    JPA_DB_URL="jdbc:h2:file:/app/data/equipos;DB_CLOSE_ON_EXIT=FALSE"
RUN mkdir -p /app/data
VOLUME ["/app/data"]
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
