#!/bin/bash
# Install Maven
curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz | tar xz
export PATH=$PWD/apache-maven-3.9.4/bin:$PATH

# Build project
mvn clean package -DskipTests