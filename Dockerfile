FROM java:8

EXPOSE 8080

ADD vueblog-0.0.1-SNAPSHOT.jar blog.jar
RUN bash -c 'touch /blog.jar'

ENTRYPOINT ["java", "-jar", "/blog.jar", "--spring.profiles.active=prd"]