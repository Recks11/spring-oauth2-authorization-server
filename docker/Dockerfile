FROM adoptopenjdk:16-jdk-hotspot as builder
WORKDIR /application
COPY .mvn .mvn
COPY src src
COPY mvnw* ./
COPY *.xml ./

RUN ./mvnw -Dspring.profiles.active=dev clean package --file pom.xml
RUN mkdir /output && cp target/*.jar /output/spring-app.jar

WORKDIR /output
RUN java -Djarmode=layertools -jar spring-app.jar extract


FROM adoptopenjdk:16-jre-hotspot
WORKDIR app
COPY --from=builder output/dependencies/ ./
COPY --from=builder output/spring-boot-loader/ ./
COPY --from=builder output/snapshot-dependencies/ ./
COPY --from=builder output/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]