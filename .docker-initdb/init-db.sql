CREATE DATABASE book_db;
CREATE USER book_service_user WITH PASSWORD 'strong_password_123';
GRANT ALL PRIVILEGES ON DATABASE book_db to book_service_user;
\c book_db
GRANT ALL ON SCHEMA public to book_service_user;