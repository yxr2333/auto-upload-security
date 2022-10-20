FROM fabric8/java-alpine-openjdk8-jdk

WORKDIR /usr/app

COPY target/auto-upload-security-1.0-SNAPSHOT.jar /usr/app

RUN cd /usr/app
RUN mv auto-upload-security-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/usr/app/app.jar"]

EXPOSE 8888