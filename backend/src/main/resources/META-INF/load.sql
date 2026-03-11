-- init-db/01-initialize.sql

-- Create Roles
INSERT INTO roles (name, level, description, created_at, updated_at) VALUES ('ADMIN', 1, 'Administrator with full access', NOW(), NOW());
INSERT INTO roles (name, level, description, created_at, updated_at) VALUES ('MANAGER', 2, 'Manager with content management access', NOW(), NOW());
INSERT INTO roles (name, level, description, created_at, updated_at) VALUES ('EDITOR', 3, 'Editor with content management access', NOW(), NOW());
INSERT INTO roles (name, level, description, created_at, updated_at) VALUES ('USER', 4, 'User with content creation access', NOW(), NOW());

-- Create Admin User
INSERT INTO users (name, email, password, role_id, created_at, updated_at) VALUES ('Admin User', 'admin@publishinghouse.com', '$2a$12$CfVua0I9HF7xCNV5QeEFd.c1GShNOYnsaPb/NKZK8f13xLdLy5fcW', 1, NOW(), NOW());

-- Create Users
INSERT INTO users (name, email, password, role_id, created_at, updated_at) VALUES ('Karan Kumar', 'karan.kumar@publishinghouse.com', '$2a$12$CfVua0I9HF7xCNV5QeEFd.c1GShNOYnsaPb/NKZK8f13xLdLy5fcW', 2, NOW(), NOW());
INSERT INTO users (name, email, password, role_id, created_at, updated_at) VALUES ('Nadir Hussain', 'nadir.hussain@publishinghouse.com', '$2a$12$CfVua0I9HF7xCNV5QeEFd.c1GShNOYnsaPb/NKZK8f13xLdLy5fcW', 3, NOW(), NOW());
INSERT INTO users (name, email, password, role_id, created_at, updated_at) VALUES ('Asif Majeed', 'asif.majeed@publishinghouse.com', '$2a$12$CfVua0I9HF7xCNV5QeEFd.c1GShNOYnsaPb/NKZK8f13xLdLy5fcW', 3, NOW(), NOW());

-- Create Employees from Users
INSERT INTO employees (user_id, phone, position, employee_id, address, department, created_at, updated_at) VALUES (2, '3955512345', 'Manager', 'EMP001', '123 Main St, Cityville', 'MERN Stack', NOW(), NOW());
INSERT INTO employees (user_id, phone, position, employee_id, address, department, created_at, updated_at) VALUES (3, '3955512346', 'Editor', 'EMP002', '456 Oak St, Townsville', 'Content', NOW(), NOW());
INSERT INTO employees (user_id, phone, position, employee_id, address, department, created_at, updated_at) VALUES (4, '3955512347', 'Artist', 'EMP003', '789 Pine St, Villageville', 'Graphic Design', NOW(), NOW());

-- Create Authors
INSERT INTO authors (name, email, biography, nationality, birth_date, created_at, updated_at) VALUES ('John Doe', 'john.doe@publishinghouse.com', 'John Doe is an acclaimed author known for his thrilling novels.', 'American', '1975-05-15', NOW(), NOW());
INSERT INTO authors (name, email, biography, nationality, birth_date, created_at, updated_at) VALUES ('Jane Smith', 'jane.smith@publishinghouse.com', 'Jane Smith is a celebrated author of historical fiction.', 'British', '1980-08-22', NOW(), NOW());
INSERT INTO authors (name, email, biography, nationality, birth_date, created_at, updated_at) VALUES ('David Johnson', 'david.johnson@publishinghouse.com', 'David Johnson is a renowned author of science fiction.', 'Canadian', '1978-11-30', NOW(), NOW());
INSERT INTO authors (name, email, biography, nationality, birth_date, created_at, updated_at) VALUES ('Emily Davis', 'emily.davis@publishinghouse.com', 'Emily Davis is an award-winning author of contemporary literature.', 'Australian', '1985-03-12', NOW(), NOW());

-- Create Publications
-- TEXTBOOK,
-- NOVEL,
-- BIOGRAPHY,
-- SCIENCE_FICTION,
-- RESEARCH_PAPER,
INSERT INTO publications (title, type, status, edition, pages, price, publication_date, isbn, description, created_at, updated_at) VALUES ('The Great Adventure', 'TEXTBOOK', 'PUBLISHED', '1', 320, 19.99, '2020-06-15', '9783161484100', 'An exhilarating adventure novel that takes readers on a journey through uncharted territories.', NOW(), NOW());
INSERT INTO publications (title, type, status, edition, pages, price, publication_date, isbn, description, created_at, updated_at) VALUES ('Mysteries of the Past', 'NOVEL', 'PUBLISHED', '2', 280, 15.99, '2019-09-10', '9781234567890', 'A captivating historical fiction that unravels the secrets of ancient civilizations.', NOW(), NOW());
INSERT INTO publications (title, type, status, edition, pages, price, publication_date, isbn, description, created_at, updated_at) VALUES ('Future Worlds', 'SCIENCE_FICTION', 'DRAFT', '1', 350, 22.50, '2021-01-20', '9780123456789', 'A thought-provoking science fiction novel exploring futuristic societies and technologies.', NOW(), NOW());
INSERT INTO publications (title, type, status, edition, pages, price, publication_date, isbn, description, created_at, updated_at) VALUES ('Contemporary Tales', 'BIOGRAPHY', 'PUBLISHED', '3', 300, 18.75, '2018-11-05', '9789876543210', 'A collection of contemporary stories that delve into modern life and relationships.', NOW(), NOW());
INSERT INTO publications (title, type, status, edition, pages, price, publication_date, isbn, description, created_at, updated_at) VALUES ('Research on AI', 'RESEARCH_PAPER', 'IN_REVIEW', '1', 150, 30.00, '2022-03-15', '9784567890123', 'An in-depth research paper on the advancements and implications of artificial intelligence.', NOW(), NOW());

-- Link Authors to Publications
INSERT INTO publication_authors (publication_id, author_id) VALUES (1, 1);
INSERT INTO publication_authors (publication_id, author_id) VALUES (2, 2);
INSERT INTO publication_authors (publication_id, author_id) VALUES (3, 3);
INSERT INTO publication_authors (publication_id, author_id) VALUES (4, 4);
INSERT INTO publication_authors (publication_id, author_id) VALUES (1, 2);
INSERT INTO publication_authors (publication_id, author_id) VALUES (3, 1);
INSERT INTO publication_authors (publication_id, author_id) VALUES (2, 4);
INSERT INTO publication_authors (publication_id, author_id) VALUES (4, 3);
INSERT INTO publication_authors (publication_id, author_id) VALUES (1, 3);
INSERT INTO publication_authors (publication_id, author_id) VALUES (2, 1);
INSERT INTO publication_authors (publication_id, author_id) VALUES (3, 4);
INSERT INTO publication_authors (publication_id, author_id) VALUES (4, 2);

