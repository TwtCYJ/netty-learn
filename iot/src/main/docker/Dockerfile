FROM java:8
VOLUME /tmp
ADD iot-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8283
EXPOSE 8282
#配置容器启动后执行的命令
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
