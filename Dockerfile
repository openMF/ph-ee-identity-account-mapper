FROM openjdk:17
EXPOSE 9090

COPY build/libs/*.jar .
CMD java -jar *.jar