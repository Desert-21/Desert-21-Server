FROM openjdk:17
MAINTAINER maciej.malewicz.it@gmail.com
COPY target/Desert21-0.0.0.jar /app.jar
RUN ls -la
RUN ls -la /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]