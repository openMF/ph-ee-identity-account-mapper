FROM openjdk:17-bullseye
EXPOSE 8080

COPY build/libs/*.jar .
CMD java -jar *.jar