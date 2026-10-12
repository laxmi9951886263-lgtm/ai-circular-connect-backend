
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .


RUN apt-get update && apt-get install -y maven && mvn clean package -DskipTests


EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar target/*.jar"]
