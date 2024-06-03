FROM openjdk:17-8.6
ADD data-service/build/libs/data-service-0.0.1-SNAPSHOT.jar project/data-service-0.0.1-SNAPSHOT.jar
CMD cd project; java -jar data-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
