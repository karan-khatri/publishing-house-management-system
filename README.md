# SWAM - Publishing House Management System

A full-stack web application for managing a publishing house's operations, including authors, publications, employees, and users.

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Services & Ports](#services--ports)
- [Default Credentials](#default-credentials)
- [Development](#development)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)

## 🎯 Overview

SWAM is a comprehensive publishing house management system that enables efficient management of:

- **Users & Authentication** - Role-based access control with JWT authentication
- **Employees** - Employee records with departments, positions, and contact information
- **Authors** - Author profiles with biographies and publication history
- **Publications** - Books and research papers with ISBN, pricing, and status tracking
- **Roles** - Hierarchical role management (Admin, Manager, Editor, User)

## 🛠 Tech Stack

### Backend
- **Framework**: Jakarta EE (JAX-RS)
- **Application Server**: WildFly 37
- **Build Tool**: Maven
- **Java Version**: 21
- **Authentication**: JWT

### Frontend
- **Framework**: Angular 20
- **Language**: TypeScript
- **Build Tool**: Angular CLI

### Database
- **Database**: PostgreSQL 16
- **ORM**: JPA/Hibernate

### DevOps
- **Containerization**: Docker & Docker Compose
- **Container Runtime**: Docker

## 📦 Prerequisites

Before running the application, ensure you have the following installed:

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)

That's it! Docker Compose will handle all other dependencies.

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url> - Not Applicable Right Now
cd swam
```

### 2. Start the Application

Run the following command from the project root directory:

```bash
docker-compose up --build
```

This command will:
- Build the backend and frontend Docker images
- Start the PostgreSQL database
- Deploy the backend application on WildFly
- Start the Angular development server
- Initialize the database with sample data

### 3. Access the Application

Once all services are running (this may take 1-2 minutes on first startup):

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **PostgreSQL**: localhost:5432

### 4. Stop the Application

To stop all services:

```bash
docker-compose down
```

To stop and remove all data (including database volumes):

```bash
docker-compose down -v
```

## 📁 Project Structure

```
swam/
├── docker-compose.yml          # Docker Compose configuration
├── backend/              # Java Jakarta EE backend
│   ├── Dockerfile
│   ├── pom.xml               # Maven configuration
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/org/swam/publishing_house/
│   │   │   │   ├── model/        # JPA entities
│   │   │   │   ├── rest/         # REST endpoints
│   │   │   │   ├── service/      # Business logic
│   │   │   │   ├── security/     # Authentication & authorization
│   │   │   │   └── mapper/       # DTO mappers
│   │   │   └── resources/
│   │   │       └── META-INF/
│   │   │           ├── persistence.xml
│   │   │           └── load.sql  # Database initialization
│   │   └── test/
│   └── target/               # Build output
└── frontend/            # Angular frontend
    ├── Dockerfile
    ├── package.json
    ├── angular.json
    └── src/
        └── app/
            ├── core/         # Core services, guards, interceptors
            ├── features/     # Feature modules
            │   ├── auth/
            │   ├── author/
            │   ├── dashboard/
            │   ├── employees/
            │   ├── login/
            │   ├── publications/
            │   └── users/
            └── shared/       # Shared components & utilities
```

## 🌐 Services & Ports

| Service | Container Name | Port(s) | Description |
|---------|---------------|---------|-------------|
| PostgreSQL | swam-postgres | 5432 | Database server |
| Backend | swam-backend | 8080, 9990 | WildFly application server |
| Frontend | swam-frontend | 4200 | Angular development server |

## 🔐 Default Credentials

The application is pre-seeded with the following test users:

| Email | Password | Role |
|-------|----------|------|
| admin@publishinghouse.com | Password123! | Admin |
| karan.kumar@publishinghouse.com | Password123! | Manager |
| nadir.hussain@publishinghouse.com | Password123! | Editor |
| asif.majeed@publishinghouse.com | Password123! | Editor |

> **Note**: All passwords in the seed data are `Password123!` (hashed with bcrypt)

## 💻 Development

### Backend Development

#### Building the Application

To build the application locally using Maven:

```bash
cd swam-backend
mvn clean package
```

This Maven command compiles the Jakarta EE application and provisions WildFly with your application deployed.

#### Running Locally (without Docker)

After building, the `target/server` directory contains a fully functional WildFly server with your application:

```bash
./target/server/bin/standalone.sh
```

The application is accessible at http://localhost:8080/

To stop the application, press `Ctrl + C` in the terminal.

#### Continuous Development

For continuous development with automatic updates on code changes:

```bash
mvn clean wildfly:dev
```

The application runs at http://localhost:8080 and automatically recompiles when you save changes to your code.

### Frontend Development

#### Installing Dependencies

```bash
cd swam-frontend
npm install
```

#### Running Development Server

```bash
ng serve
```

The application will be available at http://localhost:4200/

## 📡 API Documentation

### Base URL

```
http://localhost:8080/api
```

### Authentication

The API uses JWT (JSON Web Tokens) for authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Endpoints

#### Authentication (`/api/auth`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/login` | User login with email and password | No |
| POST | `/auth/validate-token` | Validate JWT token | No |

#### Users (`/api/users`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| POST | `/users` | Create a new user | No* | - |
| GET | `/users` | Get all users | Yes | ADMIN |
| GET | `/users/me` | Get current authenticated user | Yes | All |
| GET | `/users/{id}` | Get user by ID | Yes | All (own data) / ADMIN (any) |
| GET | `/users/role/{roleId}` | Get users by role ID | Yes | ADMIN, MANAGER |
| GET | `/users/role/name/{roleName}` | Get users by role name | Yes | ADMIN, MANAGER |
| DELETE | `/users/{id}` | Delete a user | Yes | ADMIN |
| PATCH | `/users/{id}` | Update user (partial) | Yes | All (own data) / ADMIN (any) |
| PUT | `/users/change-password` | Change password | Yes | All |

*Note: User creation endpoint is currently open for registration

#### Authors (`/api/authors`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| POST | `/authors` | Create a new author | Yes | ADMIN, MANAGER, EDITOR |
| GET | `/authors` | Get authors with filters, pagination, and sorting | Yes | ADMIN, MANAGER, EDITOR |
| GET | `/authors/{id}` | Get author by ID | Yes | USER, EDITOR, MANAGER, ADMIN |
| PATCH | `/authors/{id}` | Update author (partial) | Yes | ADMIN, MANAGER, EDITOR |
| DELETE | `/authors/{id}` | Delete an author | Yes | ADMIN |

**Query Parameters for GET `/authors`:**
- `query` - Search term
- `page` - Page number (default: 0)
- `limit` - Items per page (default: 10)
- `sortBy` - Sort field (default: name)
- `sortDir` - Sort direction: asc/desc (default: asc)

#### Publications (`/api/publications`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| POST | `/publications` | Create a new publication | Yes | ADMIN, MANAGER, EDITOR |
| GET | `/publications` | Get publications with filters, pagination, and sorting | Yes | USER, EDITOR, MANAGER, ADMIN |
| GET | `/publications/{id}` | Get publication by ID | Yes | USER, EDITOR, MANAGER, ADMIN |
| PATCH | `/publications/{id}` | Update publication (partial) | Yes | ADMIN, MANAGER, EDITOR |
| DELETE | `/publications/{id}` | Delete a publication | Yes | ADMIN |

**Query Parameters for GET `/publications`:**
- `query` - General search term
- `title` - Filter by title
- `type` - Filter by type (TEXTBOOK, NOVEL, BIOGRAPHY, SCIENCE_FICTION, RESEARCH_PAPER)
- `isbn` - Filter by ISBN
- `status` - Filter by status (PUBLISHED, DRAFT, IN_REVIEW)
- `authorId` - Filter by author ID
- `minPrice` - Minimum price
- `maxPrice` - Maximum price
- `createdAfter` - Filter by creation date (format: yyyy-MM-dd)
- `createdBefore` - Filter by creation date (format: yyyy-MM-dd)
- `page` - Page number (default: 0)
- `limit` - Items per page (default: 20)
- `sortBy` - Sort field (default: createdAt)
- `sortDir` - Sort direction: asc/desc (default: desc)

#### Employees (`/api/employees`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| POST | `/employees` | Create a new employee | Yes | ADMIN, MANAGER |
| GET | `/employees` | Get employees with filters, pagination, and sorting | Yes | ADMIN, MANAGER |
| GET | `/employees/{id}` | Get employee by ID | Yes | USER, EDITOR, MANAGER, ADMIN |
| GET | `/employees/employee-id/{employeeId}` | Get employee by employee ID | Yes | ADMIN, MANAGER |
| GET | `/employees/user/{userId}` | Get employee by user ID | Yes | USER, EDITOR, MANAGER, ADMIN |
| PATCH | `/employees/{id}` | Update employee (partial) | Yes | ADMIN, MANAGER |
| DELETE | `/employees/{id}` | Delete an employee | Yes | ADMIN |

**Query Parameters for GET `/employees`:**
- `query` - Search term
- `page` - Page number (default: 0)
- `limit` - Items per page (default: 20)
- `sortBy` - Sort field (default: name)
- `sortDir` - Sort direction: asc/desc (default: asc)

#### Roles (`/api/roles`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| POST | `/roles` | Create a new role | Yes | ADMIN |
| GET | `/roles` | Get all roles | Yes | ADMIN, MANAGER |
| GET | `/roles/{id}` | Get role by ID | Yes | ADMIN, MANAGER |
| GET | `/roles/name/{name}` | Get role by name | Yes | ADMIN, MANAGER |
| DELETE | `/roles/{id}` | Delete a role | Yes | ADMIN |

## 🗄 Database Schema

### Main Entities

- **Users**: User accounts with authentication
- **Roles**: Role definitions (Admin, Manager, Editor, User)
- **Employees**: Employee records linked to users
- **Authors**: Author profiles and biographies
- **Publications**: Books and research papers
- **Publication_Authors**: Many-to-many relationship between publications and authors

### Database Configuration

- **Database Name**: swamdb
- **Username**: swamuser
- **Password**: swampass
- **Host**: postgres-db (in Docker network) or localhost (external)
- **Port**: 5432

### Sample Data

The database is automatically initialized with:
- 4 roles (Admin, Manager, Editor, User)
- 4 users with different roles
- 3 employees
- 4 authors
- 5 publications (various types and statuses)
- Multiple author-publication relationships

## 🔧 Environment Variables

### Backend (swam-backend)

| Variable | Default Value | Description |
|----------|---------------|-------------|
| POSTGRESQL_USER | swamuser | Database username |
| POSTGRESQL_PASSWORD | swampass | Database password |
| POSTGRESQL_URL | jdbc:postgresql://postgres-db:5432/swamdb | JDBC connection URL |
| POSTGRESQL_DATABASE | swamdb | Database name |

### Database (postgres-db)

| Variable | Default Value | Description |
|----------|---------------|-------------|
| POSTGRES_DB | swamdb | Database name |
| POSTGRES_USER | swamuser | Database username |
| POSTGRES_PASSWORD | swampass | Database password |


## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

**Happy Publishing! 📚**
