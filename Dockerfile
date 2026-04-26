FROM eclipse-temurin:25-jdk
WORKDIR /app

COPY dist/pombar_portillo_mario_TFG_Server.jar app.jar
COPY lib/mysql-connector-j-9.5.0.jar mysql.jar

CMD ["java", "-cp", "app.jar:mysql.jar", "server.Server"]