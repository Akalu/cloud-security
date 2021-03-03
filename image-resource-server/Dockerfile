# At this point, BUILDER stage should have your .jar or whatever in some path
FROM openjdk:11-slim

LABEL maintainer="akalu"
LABEL com.cloud.name = "image-service"
LABEL com.cloud.version = "1.0.0"

WORKDIR /opt/app
COPY ./target/image-resource-server-1.0.0-SNAPSHOT.jar .
EXPOSE 8050
CMD [ "java", "-jar", "/opt/app/image-resource-server-1.0.0-SNAPSHOT.jar" ]