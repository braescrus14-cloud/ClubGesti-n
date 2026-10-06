FROM eclipse-temurin:21-jdk
COPY "./target/gestion-unificada-1.0.0.jar" "app.jar"
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
