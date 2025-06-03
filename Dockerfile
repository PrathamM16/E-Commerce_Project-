FROM jenkins/jenkins:lts

# Switch to root user
USER root

# Install Docker CLI
RUN apt-get update && apt-get install -y \
    docker.io \
    maven \
    curl \
    gnupg \
    ca-certificates \
    lsb-release \
    software-properties-common \
    && rm -rf /var/lib/apt/lists/*

# Install Node.js (LTS)
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs

# Verify versions
RUN java -version && mvn -v && node -v && npm -v && docker --version

# Set back to Jenkins user
USER jenkins
