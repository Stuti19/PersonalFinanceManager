#!/bin/bash
set -e

# Check if Java is already available
if command -v java &> /dev/null; then
    export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    echo "Using existing Java: $JAVA_HOME"
else
    # Download and install OpenJDK 17 manually
    echo "Downloading OpenJDK 17..."
    wget -q https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz
    tar xzf openjdk-17.0.2_linux-x64_bin.tar.gz
    export JAVA_HOME=$PWD/jdk-17.0.2
fi

echo "JAVA_HOME: $JAVA_HOME"
export PATH=$JAVA_HOME/bin:$PATH

# Verify Java
java -version

# Download and install Maven
echo "Downloading Maven..."
wget -q https://archive.apache.org/dist/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz
tar xzf apache-maven-3.9.4-bin.tar.gz
export PATH=$PWD/apache-maven-3.9.4/bin:$PATH

# Build project
echo "Building project..."
mvn clean package -DskipTests