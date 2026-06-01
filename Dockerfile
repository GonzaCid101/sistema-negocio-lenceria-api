# Solo usamos Java para encender, ya no usamos Maven para compilar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiamos el motor que ya fabricaste en tu compu
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xmx300m", "-jar", "app.jar"]