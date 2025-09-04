# Bank Management System

A modern, full-stack bank management system built with **Spring Boot** backend and **React** frontend, using **PostgreSQL** as the database.

## Features

- **Customer Management**: Add, edit, delete, and search customers
- **Account Management**: Create and manage different types of accounts (Savings, Checking, Fixed Deposit)
- **Transaction Processing**: Handle deposits, withdrawals, transfers, and interest credits
- **User Authentication**: Secure login and registration system
- **Role-based Access Control**: Different permissions for different user roles
- **Modern UI**: Responsive design with Bootstrap and React
- **RESTful API**: Clean, well-documented API endpoints
- **Real-time Database**: Powered by PostgreSQL

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend│    │ Spring Boot API │    │  Database       │
│                 │◄──►│                 │◄──►│                 │
│   - Dashboard   │    │   - Controllers │    │   - PostgreSQL  │
│   - Forms       │    │   - Services    │    │   - Real-time   │
│   - Tables      │    │   - Repositories│    │   - Auth        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Quick Start

### Prerequisites

- **Java 17** or higher
- **Node.js 16** or higher
- **Maven 3.6** or higher
- **PostgreSQL** database

### 1. Clone the Repository

```bash
git clone <repository-url>
cd BankManagementSystem
```

### 2. Set Up Database

1. Install PostgreSQL on your system
2. Create a new database named `bank_management`
3. Update the database connection details in the environment variables

### 3. Configure Environment Variables

Create a `.env` file in the `backend` directory:

```env
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/bank_management
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_database_password

# Admin Credentials
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your_secure_admin_password

# JWT Configuration
JWT_SECRET=your-very-long-and-secure-jwt-secret-key-here

# Supabase Configuration (if using)
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your_supabase_anon_key
SUPABASE_SERVICE_ROLE_KEY=your_supabase_service_role_key
```

### 4. Start the Backend

```bash
# Navigate to the backend directory
cd backend

# Run with Maven
mvn spring-boot:run
```

The Spring Boot application will start on `http://localhost:8080`

### 5. Start the Frontend

```bash
# In a new terminal, navigate to the frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

The React application will start on `http://localhost:3000`

## Database Schema

The application automatically creates the following tables:

### Customers
- `customer_id` (Primary Key)
- `first_name`, `last_name`
- `email`, `phone` (unique)
- `address`, `date_of_birth`
- `is_active`, `created_at`

### Accounts
- `account_id` (Primary Key)
- `customer_id` (Foreign Key)
- `account_number` (unique, 10 digits)
- `account_type` (SAVINGS, CHECKING, FIXED_DEPOSIT)
- `balance`, `interest_rate`
- `status`, `created_at`

### Transactions
- `transaction_id` (Primary Key)
- `account_id` (Foreign Key)
- `transaction_type` (DEPOSIT, WITHDRAWAL, TRANSFER, etc.)
- `amount`, `description`
- `reference_number`, `status`
- `transaction_date`

### Users & Roles
- `user_id`, `username`, `password`
- `email`, `roles` (many-to-many)
- Role-based access control

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Dashboard
- `GET /api/dashboard/stats` - Get dashboard statistics

### Customers
- `GET /api/customers` - Get all customers
- `GET /api/customers/{id}` - Get customer by ID
- `POST /api/customers` - Create new customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer
- `GET /api/customers/search?query={name}` - Search customers

### Accounts
- `GET /api/accounts` - Get all accounts
- `GET /api/accounts/{id}` - Get account by ID
- `POST /api/accounts` - Create new account
- `PUT /api/accounts/{id}` - Update account
- `DELETE /api/accounts/{id}` - Delete account

### Transactions
- `GET /api/transactions` - Get all transactions
- `GET /api/transactions/{id}` - Get transaction by ID
- `POST /api/transactions` - Create new transaction
- `DELETE /api/transactions/{id}` - Delete transaction

## Frontend Features

- **Responsive Dashboard** with key metrics
- **Customer Management** with search and filtering
- **Account Operations** with real-time updates
- **Transaction Processing** with validation
- **Modern UI Components** using React Bootstrap
- **Form Validation** with React Hook Form
- **Toast Notifications** for user feedback

## Security Features

- **JWT Authentication** for secure API access
- **Password Encryption** using BCrypt
- **CORS Configuration** for cross-origin requests
- **Input Validation** with Bean Validation
- **Role-based Access Control** (RBAC)
- **Environment Variables** for sensitive configuration

## Development

### Backend Development

```bash
# Run tests
mvn test

# Build JAR file
mvn clean package

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Frontend Development

```bash
# Install new dependencies
npm install 

# Run start the development
npm start
```

BankManagementSystem/
├── backend/
│   ├── src/main/java/com/bankmanagement/
│   │   ├── controller/     # REST Controllers
│   │   ├── service/        # Business Logic
│   │   ├── repository/     # Data Access Layer
│   │   ├── model/          # Entity Models
│   │   ├── dto/            # Data Transfer Objects
│   │   └── config/         # Configuration Classes
│   ├── src/main/resources/
│   │   └── application.yml # Application Configuration
│   └── pom.xml            # Maven Dependencies
├── frontend/
│   ├── src/
│   │   ├── components/     # React Components
│   │   ├── App.jsx        # Main App Component
│   │   └── index.js       # Entry Point
│   └── package.json       # NPM Dependencies
└── README.md
```