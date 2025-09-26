-- Initialize pet care system database
-- This script runs when MySQL container starts for the first time

USE pet_care_system;

-- Create additional user if needed
-- CREATE USER IF NOT EXISTS 'petcare_user'@'%' IDENTIFIED BY 'Liminghao2001';
-- GRANT ALL PRIVILEGES ON pet_care_system.* TO 'petcare_user'@'%';
-- FLUSH PRIVILEGES;

-- Insert default roles if they don't exist
INSERT IGNORE INTO role (name) VALUES 
('ROLE_ADMIN'),
('ROLE_PATIENT'), 
('ROLE_VET');

-- You can add more initialization data here as needed