# Stage 1: Build
FROM maven:3-amazoncorretto-21-alpine AS build

# Install Node.js and npm for Vaadin production build
RUN apk add --no-cache \
    nodejs \
    npm \
    python3

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application with production profile
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=cache,target=/root/.npm \
    <<EOF
    set -Eeux
    # mvn clean test \
    #     -Pproduction \
    #     -Dhttps.protocols=TLSv1.2 \
    #     -Daether.dependencyCollector.impl=bf

    mvn -B package \
        -Pproduction \
        -DskipTests \
        -Dhttps.protocols=TLSv1.2 \
        -Daether.dependencyCollector.impl=bf
EOF

# Stage 2: Runtime
FROM amazoncorretto:21-alpine-jdk

RUN apk update --no-cache && apk add --no-cache envsubst

# Set working directory
WORKDIR /app

# Copy the built artifact from build stage
COPY --from=build /app/target/radman.product.jar /app/radman.jar

# Copy Docker configuration template and entrypoint script
COPY config-files/radman.properties.docker /app/config/radman.properties.template
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

# Create config directory and log directory
RUN mkdir -p /app/config /var/log/radman

# Expose the application port
EXPOSE 8089

# Set entrypoint
ENTRYPOINT ["/app/docker-entrypoint.sh"]

# Default command - run the application with JVM flags
CMD ["java", \
     "--enable-native-access=ALL-UNNAMED", \
     "-jar", "/app/radman.jar", \
     "--spring.config.location=file:/app/config/radman.properties"]
