FROM openjdk:12
VOLUME /tmp
EXPOSE 9999
ADD ./target/springboot-servicio-config-server-0.0.1-SNAPSHOT.jar config-server.jar
ENTRYPOINT ["java","-jar","/config-server.jar"]