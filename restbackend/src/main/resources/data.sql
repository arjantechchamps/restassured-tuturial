-- Insert default roles
INSERT INTO roles(name) VALUES('USER');
INSERT INTO roles(name) VALUES('MODERATOR');
INSERT INTO roles(name) VALUES('ADMIN');

INSERT INTO ADDRESSES(id, street, city, state, zipcode,country,type) VALUES
(1, '123 Maple Street', 'Springfield', 'Illinois', '62701', 'USA','HOME'),
(2, '456 Oak Avenue', 'Springfield', 'Illinois', '62702', 'USA','WORK'),
(3, '789 Pine Road', 'Riverdale', 'New York', '10463', 'USA','HOME'),
(4, '321 Birch Lane', 'Riverdale', 'New York', '10464', 'USA','WORK'),
(5, '12 Elm Street', 'Brooklyn', 'New York', '11201', 'USA','HOME'),
(6, '34 Maple Avenue', 'Brooklyn', 'New York', '11201', 'USA','WORK');


-- Insert default users
INSERT INTO users(email, password, username, name, home_address_id, work_address_id) VALUES
('admin@test.nl', '$2a$10$svng46i/76Kt0tG37Bg39eYljIa1SVmV2QG0nTe9FdvYdF94I8vGG', 'admin', 'admin',1,2),
('user@test.nl', '$2a$10$DkmJzTXLhxexyJjouEosIu5/2rhET1lOcdfvRUziPE16XaDRtdqEK', 'user', 'user',3,4),
('moderator@test.nl', '$2a$10$TO0T8k2q5K3/Vse3LsUfnu0rq5.0M088HZh4phisR3kIDecaFEUDK', 'moderator', 'moderator',5,6);

-- Set roles for users
INSERT INTO USER_ROLES(user_id, role_id) VALUES
(1, 3),  -- Admin has ROLE_ADMIN
(1, 1),  -- Admin also has ROLE_USER
(2, 1),  -- User has ROLE_USER
(3, 2);  -- Moderator has ROLE_MODERATOR

