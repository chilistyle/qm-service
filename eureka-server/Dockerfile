FROM bellsoft/liberica-openjre-alpine:25
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
VOLUME /tmp
COPY target/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:+EnableDynamicAgentLoading --enable-native-access=ALL-UNNAMED -Dfile.encoding=UTF-8 -Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -Xss256k"
ENTRYPOINT ["java", "-jar", "app.jar"]