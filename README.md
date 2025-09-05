# VaultX Banking System

A modern, secure banking management system built with **Spring Boot** backend and **React** frontend, deployed on **Render**.

## Live Demo

**Visit my website through this link**: https://vaultx-bankmanagementsystem.onrender.com

<img width="1911" height="903" alt="image" src="https://github.com/user-attachments/assets/9c02fec0-1914-45c6-9da2-cb855495c3bd" />

## Features

- **Secure Authentication** - JWT-based authentication with role-based access
- **Customer Management** - Add, edit, delete, and search customers
- **Account Management** - Create and manage different account types (Savings, Checking, Fixed Deposit)
- **Transaction Processing** - Handle deposits, withdrawals, transfers, and interest credits
- **Dashboard** - Real-time statistics and system health monitoring
- **Responsive Design** - Modern UI that works on all devices
- **Security First** - Protected routes, secure API calls, and data encryption

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend│    │ Spring Boot API │    │  PostgreSQL     │
│   (Render)      │◄──►│   (Render)      │◄──►│   (Render)      │
│                 │    │                 │    │                 │
│   - Dashboard   │    │   - Controllers │    │   - Managed DB  │
│   - Forms       │    │   - Services    │    │   - Auto Backup │
│   - Tables      │    │   - Repositories│    │   - SSL Enabled │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Local Development

### Prerequisites
- Java 17+
- Node.js 16+
- Maven 3.6+

### Backend
```bash
cd backend
./mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## Security Features

- **JWT Authentication** - Secure token-based authentication
- **Protected Routes** - All sensitive pages require authentication
- **CORS Configuration** - Proper cross-origin request handling
- **Environment Variables** - Secrets stored securely
- **HTTPS Everywhere** - All traffic encrypted
- **Input Validation** - Server-side validation for all inputs

## Technology Stack

### Backend
- **Spring Boot 3.x** - Java framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Primary database
- **JWT** - Token-based authentication

### Frontend
- **React 18** - UI framework
- **React Router** - Client-side routing
- **React Bootstrap** - UI components
- **Axios** - HTTP client
- **Vite** - Build tool

### Deployment
- **Render** - Cloud platform
- **PostgreSQL** - Managed database
- **Nginx** - Web server (handled by Render)

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Customers
- `GET /api/customers` - List all customers
- `POST /api/customers` - Create customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

### Accounts
- `GET /api/accounts` - List all accounts
- `POST /api/accounts` - Create account
- `PUT /api/accounts/{id}` - Update account
- `DELETE /api/accounts/{id}` - Delete account

### Transactions
- `GET /api/transactions` - List all transactions
- `POST /api/transactions` - Create transaction

### Dashboard
- `GET /api/dashboard/stats` - Get dashboard statistics

## Important Notes

- **Free Tier Limits**: 750 hours/month per service
- **Database**: Managed PostgreSQL with automatic backups
- **SSL**: Free SSL certificates included
- **Updates**: Automatic deployment on git push
- **Monitoring**: Built-in health checks and logging
