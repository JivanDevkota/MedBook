-- MedBook MySQL setup
-- Run once as root:
--   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql" -u root -p < docs/mysql-setup.sql

CREATE DATABASE IF NOT EXISTS medbook CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Dedicated application account (the backend never connects as root)
CREATE USER IF NOT EXISTS 'medbook'@'localhost' IDENTIFIED BY 'medbook';
CREATE USER IF NOT EXISTS 'medbook'@'127.0.0.1' IDENTIFIED BY 'medbook';
GRANT ALL PRIVILEGES ON medbook.* TO 'medbook'@'localhost';
GRANT ALL PRIVILEGES ON medbook.* TO 'medbook'@'127.0.0.1';
FLUSH PRIVILEGES;

SELECT 'MedBook database and app user are ready.' AS status;
