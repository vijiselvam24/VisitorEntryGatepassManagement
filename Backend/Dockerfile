
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN javac -cp ".:mysql-connector-j-26.7.0.jar" *.java

CMD ["sh", "-c", "java -cp '.:mysql-connector-j-26.7.0.jar' VisitorServer"]
