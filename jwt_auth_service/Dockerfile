FROM openjdk:17-alpine3.14
WORKDIR /opt
COPY /target/*.jar /opt/jwt_auth_service.jar
ENV SERVER_PORT=8080
ENV JAVA_OPT="-Xmx512m"
EXPOSE $SERVER_PORT
ENTRYPOINT exec java $JAVA_OPT -jar jwt_auth_service.jar