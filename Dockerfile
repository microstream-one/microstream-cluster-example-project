# Build the image
FROM maven:3.9.11-eclipse-temurin-21 AS build
ENV SOURCE=/usr/local/src/cluster-storage-demo

RUN mkdir -p $SOURCE
ADD . $SOURCE
WORKDIR $SOURCE

RUN --mount=type=cache,target=/root/.m2 mvn clean package -Dapp.data.generation.enabled=false

# Run the application
FROM eclipse-temurin:21
ENV HOME=/opt/cluster-storage-demo
ARG JAR_FILE=/usr/local/src/cluster-storage-demo/target/cluster-storage-demo-*.jar

RUN mkdir -p $HOME
COPY --from=build $JAR_FILE $HOME/cluster-storage-demo.jar
WORKDIR $HOME

EXPOSE 8080

ENTRYPOINT ["java", "--add-exports", "java.base/jdk.internal.misc=ALL-UNNAMED", "--add-modules", "jdk.incubator.vector", "--enable-native-access=ALL-UNNAMED", "-jar"]
CMD ["./cluster-storage-demo.jar"]
