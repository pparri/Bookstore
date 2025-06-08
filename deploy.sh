#!/bin/bash

# Base route of the project
PROJECT_DIR=$(pwd)
TOMCAT_DIR="$PROJECT_DIR/tomcat9"
WEBAPP_NAME="bookstore"

# Creates classes folder if not exists
mkdir -p webapp/WEB-INF/classes

# Compiles ALL .java with packet structure (servlets, models, etc)
find src -name "*.java" > sources.txt
javac -d webapp/WEB-INF/classes -cp "lib/javax.servlet-api-4.0.1.jar" @sources.txt
rm sources.txt

# Clean previous deployment
rm -rf "$TOMCAT_DIR/webapps/$WEBAPP_NAME"
rm -rf "$TOMCAT_DIR/work/Catalina/localhost/$WEBAPP_NAME"

# Copy app into tomcat
cp -r webapp "$TOMCAT_DIR/webapps/$WEBAPP_NAME"

# Restart tomcat
"$TOMCAT_DIR/bin/shutdown.sh"
sleep 2
"$TOMCAT_DIR/bin/startup.sh"

echo "¡Deploy completed!"
