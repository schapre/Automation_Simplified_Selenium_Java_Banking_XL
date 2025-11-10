# Multi-stage Docker build for Automation Framework
FROM maven:3.9.4-eclipse-temurin-11 AS builder

# Set working directory
WORKDIR /app

# Copy POM file for dependency resolution
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
COPY testng.xml .

# Compile the application
RUN mvn clean compile test-compile

# Runtime stage
FROM eclipse-temurin:11-jdk

# Install browsers and dependencies
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    xvfb \
    x11vnc \
    fluxbox \
    wmctrl \
    gnupg \
    software-properties-common \
    && rm -rf /var/lib/apt/lists/*

# Install Google Chrome
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Firefox
RUN apt-get update && apt-get install -y firefox-esr \
    && rm -rf /var/lib/apt/lists/*

# Install Maven
COPY --from=builder /usr/share/maven /usr/share/maven
RUN ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# Set environment variables
ENV MAVEN_HOME=/usr/share/maven \
    MAVEN_CONFIG=/root/.m2 \
    DISPLAY=:99 \
    CHROME_BIN=/usr/bin/google-chrome \
    FIREFOX_BIN=/usr/bin/firefox

# Create application directory
WORKDIR /app

# Copy compiled application and dependencies
COPY --from=builder /app .
COPY --from=builder /root/.m2 /root/.m2

# Create necessary directories
RUN mkdir -p test-output/videos \
    && mkdir -p target/allure-results \
    && mkdir -p target/surefire-reports \
    && mkdir -p logs

# Copy automation scripts
COPY *.bat ./
RUN chmod +x *.bat

# Create entrypoint script
RUN echo '#!/bin/bash\n\
    # Start virtual display\n\
    Xvfb :99 -screen 0 1920x1080x24 &\n\
    \n\
    # Wait for display to be ready\n\
    sleep 3\n\
    \n\
    # Start window manager\n\
    fluxbox &\n\
    \n\
    # Set default browser based on environment variable\n\
    BROWSER=${BROWSER:-chrome}\n\
    TEST_SUITE=${TEST_SUITE:-all}\n\
    HEADLESS=${HEADLESS:-true}\n\
    VIDEO_RECORDING=${VIDEO_RECORDING:-false}\n\
    \n\
    echo "🐋 Starting Automation Framework in Docker"\n\
    echo "Browser: $BROWSER"\n\
    echo "Test Suite: $TEST_SUITE"\n\
    echo "Headless: $HEADLESS"\n\
    echo "Video Recording: $VIDEO_RECORDING"\n\
    \n\
    # Build test command\n\
    TEST_CMD="mvn test -Dbrowser=$BROWSER -Dheadless=$HEADLESS"\n\
    \n\
    if [ "$TEST_SUITE" != "all" ]; then\n\
    TEST_CMD="$TEST_CMD -Dtags=@$TEST_SUITE"\n\
    fi\n\
    \n\
    if [ "$VIDEO_RECORDING" = "true" ]; then\n\
    TEST_CMD="$TEST_CMD -Dvideo.recording.enabled=true"\n\
    fi\n\
    \n\
    # Execute tests\n\
    echo "🚀 Executing: $TEST_CMD"\n\
    eval $TEST_CMD\n\
    \n\
    # Generate reports\n\
    echo "📊 Generating Allure report..."\n\
    mvn allure:report\n\
    \n\
    echo "✅ Test execution completed!"\n\
    ' > /app/entrypoint.sh && chmod +x /app/entrypoint.sh

# Expose port for VNC (optional for debugging)
EXPOSE 5900

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD ps aux | grep -v grep | grep java > /dev/null || exit 1

# Set entrypoint
ENTRYPOINT ["/app/entrypoint.sh"]

# Default command
CMD ["mvn", "test"]