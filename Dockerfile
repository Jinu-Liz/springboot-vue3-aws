FROM openjdk:17
VOLUME /tmp
COPY target/springboot-vuejs-aws.jar springboot-vuejs-aws.jar
ENTRYPOINT ["java","-jar","/springboot-vuejs-aws.jar","--spring.profiles.active=prod","--DB=mysql-svc"]