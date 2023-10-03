FROM openjdk:17.0.2-jdk-slim-bullseye
EXPOSE 8080

COPY build/libs/*.jar .
CMD java -jar *.jar
