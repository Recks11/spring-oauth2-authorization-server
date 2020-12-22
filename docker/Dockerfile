FROM adoptopenjdk:14-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM adoptopenjdk:14-jre-hotspot
WORKDIR auth-server
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

EXPOSE 8000

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]