#Create Jar with maven install
mvn clean install

#Create a docker file with following content
FROM openjdk:8-jre-alpine3.9

ENV PORT 8080
EXPOSE 8080

# copy the packaged jar file into our docker image
COPY target/code-challenge-0.0.1-SNAPSHOT.jar /code-challenge.jar
 
# set the startup command to execute the jar
CMD ["java", "-jar", "/code-challenge.jar"]

# Build docker container
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker build -t code-challenge:latest .
[+] Building 0.4s (7/7) FINISHED																
 => [internal] load build definition from Dockerfile                                             0.0s
 => => transferring dockerfile: 304B                                                             0.0s
 => [internal] load .dockerignore                                                                0.0s
 => => transferring context: 2B                                                                  0.0s
 => [internal] load metadata for docker.io/library/openjdk:8-jre-alpine3.9                       0.0s
 => [internal] load build context                                                                0.2s
 => => transferring context: 16.57MB                                                             0.1s
 => [1/2] FROM docker.io/library/openjdk:8-jre-alpine3.9                                         0.1s
 => => resolve docker.io/library/openjdk:8-jre-alpine3.9                                         0.0s
 => [2/2] COPY target/code-challenge-0.0.1-SNAPSHOT.jar /code-challenge.jar                      0.1s
 => exporting to image                                                                           0.1s
 => => exporting layers                                                                          0.1s
 => => writing image sha256:4b3defefeb53edf2085ce1a3b1a24eb8fe44d008d446dddff20e582c8c69d85c     0.0s
 => => naming to docker.io/library/code-challenge:latest                                         0.0s

#Display list of images
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker image list
REPOSITORY                           TAG                        IMAGE ID       CREATED          SIZE
code-challenge                       latest                     4b3defefeb53   13 seconds ago   101MB

#Docker run the application
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker run -d -p 8080:8080 code-challenge:latest
050e5e056e6308ab3db6252bbab630640527e9950b5500b082ec6b461c0f160a

#Check the status of the container
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker ps
CONTAINER ID   IMAGE                   COMMAND                  CREATED          STATUS          PORTS                    NAMES
050e5e056e63   code-challenge:latest   "java -jar /code-cha…"   59 seconds ago   Up 57 seconds   0.0.0.0:8080->8080/tcp   nifty_nash

#Test the URL
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>curl http://localhost:8080
check cities connected ex: http://localhost:8080/connected?origin=Boston&destination=Newark

#Stop the docker container
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker stop 050e5e056e63
050e5e056e63

#Check the status of the container
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES

#Delete Image with IMAGE ID
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker image rm -f 4b3defefeb53
Untagged: code-challenge:latest
Deleted: sha256:4b3defefeb53edf2085ce1a3b1a24eb8fe44d008d446dddff20e582c8c69d85c