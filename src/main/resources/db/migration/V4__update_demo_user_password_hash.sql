-- Update demo user password hash to bcrypt format for existing databases
UPDATE users
SET password_hash = '$2a$10$YJVTiVuQZLHLvpnyRBLKbuTTazHDhGg07sU42xzAoAVImtZKqUhhy'
WHERE username = 'admin' AND password_hash = 'admin123';
