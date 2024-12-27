FROM openjdk:19-jdk-slim
LABEL maintainer="Arjan Assink arjan@techchamps.io" \
      version="1.0" \
      description="A Docker image for restbackend"
EXPOSE 8085
COPY target/restbackend-0.0.1-SNAPSHOT.jar restbackend-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","restbackend-0.0.1-SNAPSHOT.jar"]