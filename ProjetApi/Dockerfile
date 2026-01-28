# Étape 1 : On utilise une image qui contient Maven pour compiler (La Cuisine)
FROM maven:3.9-eclipse-temurin-19 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# C'est ici que Docker va transformer votre nouveau code Java en JAR
RUN mvn clean package -DskipTests

# Étape 2 : On garde juste le résultat pour le lancer (Le Service)
FROM eclipse-temurin:19-jdk
WORKDIR /app
# On récupère le gâteau tout chaud de l'étape 1
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]