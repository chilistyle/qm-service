FROM maven:3.9.11-eclipse-temurin-25 AS builder
WORKDIR /workspace

COPY . .
RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests -pl library-service -am package

FROM bellsoft/liberica-openjre-alpine:25

WORKDIR /deployments

COPY --from=builder /workspace/library-service/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder /workspace/library-service/target/quarkus-app/*.jar /deployments/
COPY --from=builder /workspace/library-service/target/quarkus-app/app/ /deployments/app/
COPY --from=builder /workspace/library-service/target/quarkus-app/quarkus/ /deployments/quarkus/

ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
EXPOSE 8100

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /deployments/quarkus-run.jar" ]