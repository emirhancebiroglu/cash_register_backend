FROM openjdk:17-alpine3.14
WORKDIR /opt
COPY /target/*.jar /opt/user_management_service.jar
ENV SERVER_PORT=8081
ENV JAVA_OPT="-Xmx512m"
EXPOSE $SERVER_PORT
ENTRYPOINT exec java $JAVA_OPT -jar user_management_service.jar