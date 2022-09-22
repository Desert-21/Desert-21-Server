FROM openjdk:17
MAINTAINER maciej.malewicz.it@gmail.com
COPY target/Desert21-0.0.0.jar /jar
ENTRYPOINT ["java","-jar","/jar/Desert21-0.0.0.jar"]