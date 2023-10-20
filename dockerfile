FROM gradle:8.4-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM tomcat

COPY --from=build /home/gradle/src/build/libs/*.war /usr/local/tomcat/webapps/
