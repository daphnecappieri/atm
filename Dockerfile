FROM openjdk:8-jdk-alpine
COPY target/atm-0.0.1-SNAPSHOT.jar atm-application-1.0.0.jar
ENTRYPOINT ["java","-jar","/atm-application-1.0.0.jar"]ge-server-1.0.0.jar"]