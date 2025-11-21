# ---- build stage ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Maven Wrapper とソース一式をコピー
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Jar をビルド
RUN chmod +x mvnw && ./mvnw -B -DskipTests clean package

# ---- run stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/todoapp-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

ENV JAVA_OPTS=""

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
