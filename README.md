# VaultX Banking System

A modern, secure banking management system built with **Spring Boot** backend and **React** frontend, deployed on **Render**.

## 🚀 Live Demo

- **Frontend**: https://vaultx-banking-frontend.onrender.com
- **Backend API**: https://vaultx-banking-api.onrender.com/api

## ✨ Features

- **🔐 Secure Authentication** - JWT-based authentication with role-based access
- **👥 Customer Management** - Add, edit, delete, and search customers
- **💳 Account Management** - Create and manage different account types (Savings, Checking, Fixed Deposit)
- **💰 Transaction Processing** - Handle deposits, withdrawals, transfers, and interest credits
- **📊 Dashboard** - Real-time statistics and system health monitoring
- **📱 Responsive Design** - Modern UI that works on all devices
- **🛡️ Security First** - Protected routes, secure API calls, and data encryption

## 🏗️ Architecture

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

## 🚀 Quick Deploy to Render

### Prerequisites
- GitHub account
- Render account (free at [render.com](https://render.com))


### 2. Deploy Backend
1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click "New +" → "Web Service"
3. Connect your GitHub repository
4. Configure:
   - **Name**: `vaultx-banking-api`
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/bank-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`
   - **Plan**: `Starter` (Free)

5. Add Environment Variables:
   ```
   SPRING_PROFILES_ACTIVE=prod
   JWT_SECRET=your-super-secure-jwt-secret-key-here
   ADMIN_USERNAME=admin
   ADMIN_PASSWORD=your-secure-admin-password
   FRONTEND_URL=https://vaultx-banking-frontend.onrender.com
   ```

6. Add Database:
   - Click "Add Database" → "PostgreSQL"
   - Name: `vaultx-postgres`
   - Copy the connection string to `DATABASE_URL` environment variable

7. Deploy!

### 3. Deploy Frontend
1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click "New +" → "Static Site"
3. Connect your GitHub repository
4. Configure:
   - **Name**: `vaultx-banking-frontend`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist`
   - **Plan**: `Starter` (Free)

5. Add Environment Variable:
   ```
   VITE_API_URL=https://vaultx-banking-api.onrender.com/api
   ```

6. Deploy!

## 🔧 Local Development

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

## 🛡️ Security Features

- **JWT Authentication** - Secure token-based authentication
- **Protected Routes** - All sensitive pages require authentication
- **CORS Configuration** - Proper cross-origin request handling
- **Environment Variables** - Secrets stored securely
- **HTTPS Everywhere** - All traffic encrypted
- **Input Validation** - Server-side validation for all inputs

## 📊 Technology Stack

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

## 🔄 API Endpoints

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

## 🚨 Important Notes

- **Free Tier Limits**: 750 hours/month per service
- **Database**: Managed PostgreSQL with automatic backups
- **SSL**: Free SSL certificates included
- **Updates**: Automatic deployment on git push
- **Monitoring**: Built-in health checks and logging

## 📞 Support

- **Documentation**: [RENDER_DEPLOYMENT.md](./RENDER_DEPLOYMENT.md)
- **Issues**: Create an issue in the repository
- **Render Support**: [render.com/docs](https://render.com/docs)

## 📄 License

This project is licensed under the MIT License.

---

**Built with ❤️ for secure banking management**