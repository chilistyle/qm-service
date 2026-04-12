FROM maven:3.9.11-eclipse-temurin-25 AS builder
WORKDIR /workspace

COPY . .
RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests -pl eureka-server -am package

FROM bellsoft/liberica-openjre-alpine:25
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
VOLUME /tmp
COPY --from=builder /workspace/eureka-server/target/eureka-server-1.0-SNAPSHOT.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:+EnableDynamicAgentLoading --enable-native-access=ALL-UNNAMED -Dfile.encoding=UTF-8 -Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -Xss256k"
ENTRYPOINT ["java", "-jar", "app.jar"]