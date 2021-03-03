# At this point, BUILDER stage should have your .jar or whatever in some path
FROM openjdk:11-slim

LABEL maintainer="akalu"
LABEL com.cloud.name = "image-client"
LABEL com.cloud.version = "1.0.0"

WORKDIR /opt/app
COPY ./target/image-client-1.0.0-SNAPSHOT.jar .
EXPOSE 8051
CMD [ "java", "-jar", "/opt/app/image-client-1.0.0-SNAPSHOT.jar" ]