CREATE DATABASE IF NOT EXISTS bookstore;

CREATE USER IF NOT EXISTS 'mysql'@'localhost' IDENTIFIED BY 'mysql';

GRANT ALL PRIVILEGES ON bookstore.* TO 'mysql'@'localhost';

FLUSH PRIVILEGES;
