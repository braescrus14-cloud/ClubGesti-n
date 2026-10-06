FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY ./target/gestion-unificada-1.0.0.jar app.jar
ENV SERVER_ADDRESS=0.0.0.0
ENV PORT=8092
ENV JPA_DB_URL="jdbc:h2:file:/app/data/equipos;DB_CLOSE_ON_EXIT=FALSE"
RUN mkdir -p /app/data
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
