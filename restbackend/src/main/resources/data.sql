-- Insert default roles
INSERT INTO roles(name) VALUES('USER');
INSERT INTO roles(name) VALUES('MODERATOR');
INSERT INTO roles(name) VALUES('ADMIN');



-- Insert default users
INSERT INTO users(email, password, username, name) VALUES
('admin@test.nl', '$2a$10$svng46i/76Kt0tG37Bg39eYljIa1SVmV2QG0nTe9FdvYdF94I8vGG', 'admin', 'admin'),
('user@test.nl', '$2a$10$DkmJzTXLhxexyJjouEosIu5/2rhET1lOcdfvRUziPE16XaDRtdqEK', 'user', 'user'),
('moderator@test.nl', '$2a$10$TO0T8k2q5K3/Vse3LsUfnu0rq5.0M088HZh4phisR3kIDecaFEUDK', 'moderator', 'moderator');

-- Set roles for users
INSERT INTO USER_ROLES(user_id, role_id) VALUES
(1, 3),  -- Admin has ROLE_ADMIN
(1, 1),  -- Admin also has ROLE_USER
(2, 1),  -- User has ROLE_USER
(3, 2);  -- Moderator has ROLE_MODERATOR

