INSERT INTO users (username, email, password_hash, active)
SELECT 'admin', 'admin@example.com', '$2a$10$YJVTiVuQZLHLvpnyRBLKbuTTazHDhGg07sU42xzAoAVImtZKqUhhy', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);

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

UPDATE users
SET password_hash = '$2a$10$YJVTiVuQZLHLvpnyRBLKbuTTazHDhGg07sU42xzAoAVImtZKqUhhy'
WHERE username = 'admin' AND password_hash = 'admin123';
