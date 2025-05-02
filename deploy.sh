#!/bin/bash

# Ruta base del proyecto
PROJECT_DIR=$(pwd)
TOMCAT_DIR="$PROJECT_DIR/tomcat9"
WEBAPP_NAME="bookstore"

# Crear carpeta de clases si no existe
mkdir -p webapp/WEB-INF/classes

# Compilar TODOS los .java con estructura de paquetes (servlets, model, etc)
find src -name "*.java" > sources.txt
javac -d webapp/WEB-INF/classes -cp "lib/javax.servlet-api-4.0.1.jar" @sources.txt
rm sources.txt

# Limpiar deploy anterior
rm -rf "$TOMCAT_DIR/webapps/$WEBAPP_NAME"
rm -rf "$TOMCAT_DIR/work/Catalina/localhost/$WEBAPP_NAME"

# Copiar app en tomcat
cp -r webapp "$TOMCAT_DIR/webapps/$WEBAPP_NAME"

# Reiniciar tomcat
"$TOMCAT_DIR/bin/shutdown.sh"
sleep 2
"$TOMCAT_DIR/bin/startup.sh"

echo "¡Deploy completed!"
