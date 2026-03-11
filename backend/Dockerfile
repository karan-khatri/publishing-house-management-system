# Multi-stage build
FROM maven:3.9.11-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package wildfly:package -DskipTests

# --- Stage 2: Deploy WAR to WildFly ---
FROM eclipse-temurin:21.0.8_9-jdk

WORKDIR /opt/app

COPY --from=builder /app/target/server ./wildfly

RUN chmod +x /opt/app/wildfly/bin/standalone.sh

EXPOSE 8080 9990

# Start WildFly
CMD ["/opt/app/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]