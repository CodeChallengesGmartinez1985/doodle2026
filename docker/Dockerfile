# ============================================================
# Stage 1: Compile
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS compile
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean compile -B

# ============================================================
# Stage 2: Testing
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS testing
WORKDIR /app
COPY --from=compile /root/.m2 /root/.m2
COPY --from=compile /app ./
RUN mvn test -B

# ============================================================
# Stage 3: Packaging
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS packaging
WORKDIR /app
COPY --from=compile /root/.m2 /root/.m2
COPY --from=testing /app ./
RUN mvn package -DskipTests -B && \
    mv target/*.jar target/app.jar && \
    rm -rf /root/.m2/repository

# ============================================================
# Stage 4: Runtime
# ============================================================
# Note: Eclipse Temurin doesn't provide JRE Alpine images for Java 17
# Using the standard JRE image which is still lightweight (~200MB)
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring
COPY --from=packaging /app/target/app.jar ./app.jar
RUN chown -R spring:spring /app
USER spring:spring
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=standalone
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
CMD []
