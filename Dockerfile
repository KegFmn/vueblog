FROM java:8

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/usr/data/vueblog-0.0.1-SNAPSHOT.jar"]