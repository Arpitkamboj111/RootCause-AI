# Build 
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw -B -f pom.xml -pl incident-correlation-service -am clean package -DskipTests

RUN set -eux; \
    JAR="$(find incident-correlation-service/target -maxdepth 1 -type f -name '*.jar' ! -name '*.jar.original' | head -n 1)"; \
    test -n "$JAR"; \
    cp "$JAR" /app/app.jar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/app.jar app.jar

EXPOSE 8092

ENTRYPOINT ["java", "-jar", "app.jar"]