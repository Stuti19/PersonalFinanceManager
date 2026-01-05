#!/bin/bash
set -e

# Install OpenJDK 17
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk

# Find and set JAVA_HOME
export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
echo "JAVA_HOME set to: $JAVA_HOME"

# Verify Java installation
java -version
javac -version

# Install Maven
wget https://archive.apache.org/dist/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz
tar xzf apache-maven-3.9.4-bin.tar.gz
export PATH=$PWD/apache-maven-3.9.4/bin:$PATH

# Verify Maven installation
mvn -version

# Build project
mvn clean package -DskipTests