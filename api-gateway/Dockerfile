FROM bellsoft/liberica-openjre-alpine:25
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
VOLUME /tmp
COPY target/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:+EnableDynamicAgentLoading --enable-native-access=ALL-UNNAMED -Dfile.encoding=UTF-8 -XX:MaxRAMPercentage=70.0 -XX:MaxMetaspaceSize=128M -XX:ActiveProcessorCount=1 -XX:+UseG1GC"
ENTRYPOINT ["java", "-jar", "app.jar"]