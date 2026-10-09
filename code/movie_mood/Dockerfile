FROM eclipse-temurin:26-jdk AS build
WORKDIR /workspace

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY src ./src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:26-jre-jammy
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring --home-dir /home/spring --create-home spring

COPY --from=build /workspace/target/*.jar /app/app.jar
RUN chown -R spring:spring /app

USER spring

EXPOSE 8080

ENV SERVER_PORT=8080
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${SERVER_PORT:-8080} -jar /app/app.jar"]
