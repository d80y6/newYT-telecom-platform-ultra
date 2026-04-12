FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl tzdata && cp /usr/share/zoneinfo/Asia/Aden /etc/localtime && echo "Asia/Aden" > /etc/timezone
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -s /bin/sh -D appuser
RUN mkdir -p /app/logs /app/config && chown -R appuser:appgroup /app
WORKDIR /app
COPY bss-core/target/bss-core-1.0.0.jar app.jar
USER appuser
EXPOSE 8080 8443 9090
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 CMD curl -f http://localhost:8080/api/v1/actuator/health || exit 1
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]