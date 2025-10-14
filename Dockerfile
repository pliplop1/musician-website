# =============================================================
# Dockerfile multi-stage pour Duo Black & White Website
# =============================================================
# Stage 1: Build frontend Vue.js et Tailwind CSS
# Stage 2: Build backend Spring Boot avec frontend intégré
# Stage 3: Runtime optimisé
# =============================================================

# ============================================================
# STAGE 1: Build Frontend (Tailwind CSS + Vue.js)
# ============================================================
FROM node:22.17.0-alpine AS frontend-builder

# Installer les dépendances système nécessaires
RUN apk add --no-cache python3 make g++

WORKDIR /app

# Copier les fichiers de configuration npm
COPY frontend/package*.json ./frontend/
COPY vue-frontend/package*.json ./vue-frontend/

# Installer les dépendances npm
RUN cd frontend && npm ci
RUN cd vue-frontend && npm ci

# Copier les sources frontend
COPY frontend/ ./frontend/
COPY vue-frontend/ ./vue-frontend/

# Builder Tailwind CSS
RUN cd frontend && npm run build:css

# Builder Vue.js application
RUN cd vue-frontend && npm run build

# ============================================================
# STAGE 2: Build Backend Spring Boot
# ============================================================
FROM maven:3.9.5-eclipse-temurin-17 AS backend-builder

WORKDIR /app

# Copier les fichiers Maven
COPY pom.xml ./
COPY src/ ./src/

# Copier les frontends buildés depuis le stage précédent
# Copier le CSS généré par Tailwind
# Le build frontend a généré le CSS dans /app/src/main/resources/static/css
COPY --from=frontend-builder /app/src/main/resources/static/css/style.css ./src/main/resources/static/css/style.css
COPY --from=frontend-builder /app/vue-frontend/dist/ ./src/main/resources/static/vue/

# Builder l'application Spring Boot (sans executer les builds frontend)
RUN mvn clean package -DskipTests -Dfrontend.skip=true

# ============================================================
# STAGE 3: Runtime - Image optimisée
# ============================================================
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="Duo Black & White <dumoulin.marilyne@gmail.com>"
LABEL description="Site web professionnel pour le Duo Black & White"
LABEL version="1.0.0"

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring

# Créer les répertoires nécessaires
RUN mkdir -p /app/uploads/photos /app/uploads/music /app/uploads/videos /app/logs && \
    chown -R spring:spring /app

WORKDIR /app

# Copier le JAR depuis le builder
COPY --from=backend-builder /app/target/*.jar app.jar

# Changer l'ownership
RUN chown -R spring:spring /app

# Utiliser l'utilisateur non-root
USER spring:spring

# Exposer le port
EXPOSE 8080

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xms512m -Xmx1024m" \
    SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
