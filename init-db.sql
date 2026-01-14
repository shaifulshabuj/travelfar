-- Initialize database with sample data for testing
-- This script runs automatically when Docker container starts

-- Create hotels table if not exists (JPA will create it, this is backup)
CREATE TABLE IF NOT EXISTS hotels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    rating DOUBLE NOT NULL,
    description VARCHAR(1000),
    total_rooms INT,
    available_rooms INT,
    INDEX idx_city (city),
    INDEX idx_price (price_per_night)
);

-- Insert sample hotel data for Tokyo
INSERT INTO hotels (name, city, price_per_night, rating, description, total_rooms, available_rooms) VALUES
('Grand Hotel Tokyo', 'Tokyo', 15000.00, 4.5, 'Luxury hotel in the heart of Tokyo with stunning city views', 100, 50),
('Business Inn Tokyo', 'Tokyo', 8000.00, 4.0, 'Affordable business hotel near Tokyo Station', 80, 40),
('Sakura Resort Tokyo', 'Tokyo', 20000.00, 4.8, 'Premium resort with traditional Japanese hospitality', 60, 30),
('Budget Stay Tokyo', 'Tokyo', 5000.00, 3.5, 'Clean and comfortable budget accommodation', 120, 80),
('Executive Suites Tokyo', 'Tokyo', 18000.00, 4.6, 'Modern executive suites for business travelers', 70, 35);

-- Insert sample hotel data for Osaka
INSERT INTO hotels (name, city, price_per_night, rating, description, total_rooms, available_rooms) VALUES
('Osaka Castle Hotel', 'Osaka', 12000.00, 4.4, 'Historic hotel with views of Osaka Castle', 90, 45),
('Namba Business Hotel', 'Osaka', 7000.00, 4.1, 'Convenient location in Namba district', 100, 60),
('Umeda Grand Hotel', 'Osaka', 16000.00, 4.7, 'Luxurious accommodation in Umeda area', 80, 40),
('Osaka Bay Resort', 'Osaka', 14000.00, 4.5, 'Beautiful resort near Osaka Bay', 75, 35);

-- Insert sample hotel data for Kyoto
INSERT INTO hotels (name, city, price_per_night, rating, description, total_rooms, available_rooms) VALUES
('Kyoto Traditional Inn', 'Kyoto', 25000.00, 4.9, 'Authentic Japanese ryokan experience', 30, 10),
('Kyoto Station Hotel', 'Kyoto', 11000.00, 4.3, 'Modern hotel near Kyoto Station', 95, 50),
('Gion Luxury Suites', 'Kyoto', 30000.00, 5.0, 'Premium suites in the historic Gion district', 40, 15),
('Arashiyama Resort', 'Kyoto', 18000.00, 4.6, 'Peaceful resort in Arashiyama bamboo grove area', 50, 25);

-- Create reservations table (JPA will handle this, but backup structure)
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    guest_name VARCHAR(200) NOT NULL,
    guest_email VARCHAR(200) NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    guests INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    version BIGINT,
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_check_in_out (check_in, check_out)
);
