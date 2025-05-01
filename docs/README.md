# Online Bookstore

## Installation Instructions

- Java JDK 8 or higher
- Apache Tomcat 9 or higher
- MariaDB (installed and running)
- Web browser (Chrome, Firefox, etc.)

- Run ./deploy.sh

## Extra libraries/snippets

- javax.servlet



javac -d webapp/WEB-INF/classes -cp "lib/javax.servlet-api-4.0.1.jar" src/servlets/HelloServlet.java

sudo cp -r webapp /var/lib/tomcat9/webapps/bookstore

sudo systemctl restart tomcat9

cp -r webapp tomcat9/webapps/bookstore

cd tomcat9/bin

chmod +x *.sh

./startup.sh // ./tomcat9/bin/startup.sh 
                ./tomcat9/bin/shutdown.sh

http://localhost:8080/bookstore/hello

----- javac -d webapp/WEB-INF/classes -cp "lib/javax.servlet-api-4.0.1.jar" src/servlets/HelloServlet.java

javac -d webapp/WEB-INF/classes -cp "lib/javax.servlet-api-4.0.1.jar" src/servlets/HelloServlet.java
rm -rf tomcat9/webapps/bookstore
rm -rf tomcat9/work/Catalina/localhost/bookstore
cp -r webapp tomcat9/webapps/bookstore
./tomcat9/bin/shutdown.sh
./tomcat9/bin/startup.sh

cat tomcat9/logs/catalina.out | tail -n 50

ALTER USER 'mysql'@'localhost' IDENTIFIED BY 'unaContraseñaSegura';
FLUSH PRIVILEGES;

mariadb -u mysql -p < sql/schema.sql

DROP DATABASE IF EXISTS bookstore;

USE bookstore;
SHOW TABLES;
DESCRIBE users;

<button onclick="history.back()">⬅️ Go Back</button>

