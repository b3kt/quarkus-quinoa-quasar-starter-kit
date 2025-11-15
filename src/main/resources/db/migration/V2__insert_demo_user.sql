-- Insert demo user (password: admin123)
-- Note: In production, passwords should be properly hashed (BCrypt, Argon2, etc.)
-- PostgreSQL compatible version

-- Insert demo user if not exists
INSERT INTO users (username, email, password_hash, active) 
VALUES ('admin', 'admin@example.com', 'admin123', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Insert roles for demo user
INSERT INTO user_roles (user_id, role)
SELECT u.id, 'user' 
FROM users u 
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur 
    WHERE ur.user_id = u.id AND ur.role = 'user'
  );

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'admin' 
FROM users u 
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur 
    WHERE ur.user_id = u.id AND ur.role = 'admin'
  );

