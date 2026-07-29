-- Run this in MySQL Workbench before running the project

CREATE DATABASE IF NOT EXISTS hotel;

USE hotel;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100),
    password VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_no VARCHAR(20) NOT NULL,
    type VARCHAR(50),
    price DOUBLE DEFAULT 0,
    status VARCHAR(20) DEFAULT 'Available'
);

CREATE TABLE IF NOT EXISTS guests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    address VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT,
    room_id INT,
    check_in VARCHAR(20),
    check_out VARCHAR(20),
    status VARCHAR(20) DEFAULT 'Booked',
    total DOUBLE DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Default admin user
INSERT INTO users (username, email, password) VALUES ('admin', 'admin@hotel.com', 'admin123');

-- Sample rooms
INSERT INTO rooms (room_no, type, price, status) VALUES
('101', 'Single', 1500, 'Available'),
('102', 'Double', 2500, 'Available'),
('103', 'Suite', 5000, 'Available'),
('104', 'Deluxe', 3500, 'Available'),
('105', 'Family', 4000, 'Available');
select * from users;