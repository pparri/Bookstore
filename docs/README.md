# BOOKSTORE DOCUMENTATION

## Installation Instructions

- Java JDK 8 or higher
- Apache Tomcat 9 or higher
- MariaDB (installed and running)
- Web browser (Chrome, Firefox, etc.)

- Clone, download or untar the project to your local machine:

    git clone https://github.com/pparri/bookstore.git or untar the project
    cd bookstore

- Import the bookstore database schema:

    mariadb -u mysql -p bookstore < sql/schema.sql
    **(password is: "mysql")

- Compile the project and deploy tomcat:
    
    ./deploy_init.sh
    For restarting use:
    ./deploy.sh

- Access the app:

    http://localhost:8080/bookstore


## Extra libraries/snippets

- javax.servlet api (version 4.0.1)
- MariaDB JDBC Driver


## DATABASE SCHEMA

CREATE DATABASE bookstore;
USE bookstore;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    is_admin BOOLEAN DEFAULT FALSE
);

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    description TEXT,
    price DECIMAL(10, 2),
    quantity INT,
    cover_image VARCHAR(255)
);

CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    book_id INT,
    quantity INT,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

## LIST OF API CALLS

Authentication:

    POST /bookstore/login – login a user
    POST /bookstore/register – register a new user
    GET /bookstore/logout – logout user

Admin:

    GET /bookstore/admin-dashboard – access admin panel
    GET/POST /bookstore/admin-dashboard/add-book – add a new book
    GET /bookstore/admin-dashboard/list-books – view books
    POST /bookstore/admin-dashboard/delete-books – delete books
    GET/POST /bookstore/admin-dashboard/view-reservations – view/delete reservations

User:

    GET /bookstore/dashboard - user dashboard
    GET /bookstore/dashboard/search-books - search books by title/author
    GET /bookstore/dashboard/detailed-book?bookId=... - view book details
    POST /bookstore/cart/add - add book to cart
    GET /bookstore/cart/view - view cart
    POST /bookstore/cart/checkout - confirm cart / make reservation
    GET /bookstore/dashboard/my-reservations - view personal reservations