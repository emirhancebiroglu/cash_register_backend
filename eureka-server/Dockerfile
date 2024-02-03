FROM openjdk:17-alpine3.14
WORKDIR /opt
COPY target/*.jar /opt/app.jar
ENV SERVER_PORT=8761
ENV JAVA_OPT="-Xmx512m"
EXPOSE $SERVER_PORT
ENTRYPOINT exec java $JAVA_OPT -jar app.jar