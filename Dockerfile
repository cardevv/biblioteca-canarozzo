# Etapa 1 (build): compila o projeto e gera o .jar com Maven + Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Copia o pom primeiro para aproveitar o cache das dependencias
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
# Os testes rodam no desenvolvimento (mvn test); aqui so empacotamos
RUN mvn -q package -DskipTests

# Etapa 2 (execucao): imagem menor, so com o Java 21 (JRE) e o .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/biblioteca-1.0.0.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
