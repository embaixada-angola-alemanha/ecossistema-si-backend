FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Ecossistema Digital <dev@embaixada-angola.site>"
LABEL service="ecossistema-si-backend"
LABEL description="Sistema de Informacao - Backend API"

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

COPY target/*.jar app.jar

USER app

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
