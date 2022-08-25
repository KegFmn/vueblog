FROM openjdk:8-jre-alpine

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/vueblog-0.0.1-SNAPSHOT.jar /usr/data/vueblog-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/usr/data/vueblog-0.0.1-SNAPSHOT.jar"]