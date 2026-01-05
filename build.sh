#!/bin/bash
set -e

# Use pre-installed Java if available
if command -v java &> /dev/null; then
    export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    echo "Using existing Java: $JAVA_HOME"
else
    # Install OpenJDK 17 without sudo
    apt-get update
    apt-get install -y openjdk-17-jdk
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
fi

echo "JAVA_HOME: $JAVA_HOME"
export PATH=$JAVA_HOME/bin:$PATH

# Verify Java
java -version

# Install Maven
wget -q https://archive.apache.org/dist/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz
tar xzf apache-maven-3.9.4-bin.tar.gz
export PATH=$PWD/apache-maven-3.9.4/bin:$PATH

# Build project
mvn clean package -DskipTests