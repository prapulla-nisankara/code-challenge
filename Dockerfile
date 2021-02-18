#FROM openjdk:8-jre-alpine3.9

#ENV PORT 8080
#EXPOSE 8080

# copy the packaged jar file into our docker image
#COPY target/code-challenge-0.0.1-SNAPSHOT.jar /code-challenge.jar

#COPY codechallenge.yaml /codechallenge.yaml
 
# set the startup command to execute the jar
#CMD ["java", "-jar", "/code-challenge.jar"]


FROM openjdk:alpine

WORKDIR .

ADD target/code-challenge-0.0.1-SNAPSHOT.jar code-challenge.jar

ADD codechallenge.yaml codechallenge.yaml

CMD java -jar code-challenge.jar server codechallenge.yaml