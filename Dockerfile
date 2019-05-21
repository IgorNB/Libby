FROM java:8-jdk-alpine

COPY ./target/libby-1.0-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch libby-1.0-SNAPSHOT.jar'

ENTRYPOINT ["java","-jar", "libby-1.0-SNAPSHOT.jar"]

EXPOSE 8090