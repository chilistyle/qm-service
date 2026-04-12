FROM maven:3.9.11-eclipse-temurin-25 AS builder
WORKDIR /workspace

COPY . .
RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests -pl api-gateway -am package

FROM bellsoft/liberica-openjre-alpine:25
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
VOLUME /tmp
COPY --from=builder /workspace/api-gateway/target/api-gateway-1.0-SNAPSHOT.jar app.jar

ENV JAVA_TOOL_OPTIONS="-XX:+EnableDynamicAgentLoading --enable-native-access=ALL-UNNAMED -Dfile.encoding=UTF-8 -Xms128m -Xmx768m -XX:+UseG1GC"
ENTRYPOINT ["java", "-jar", "app.jar"]