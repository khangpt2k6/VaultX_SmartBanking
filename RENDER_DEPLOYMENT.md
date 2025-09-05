# VaultX Banking System - Render Deployment Guide

## 🚀 Deploy to Render (Recommended for Production)

Render is perfect for your VaultX banking system! It supports both Spring Boot backend and React frontend with automatic deployments.

---

## 📋 Prerequisites

1. **GitHub Repository**: Push your code to GitHub
2. **Render Account**: Sign up at [render.com](https://render.com)
3. **Database**: Render PostgreSQL (included in deployment)

---

## 🔧 Step-by-Step Deployment

### **Step 1: Prepare Your Repository**

1. **Push to GitHub:**
   ```bash
   git add .
   git commit -m "Prepare for Render deployment"
   git push origin main
   ```

2. **Verify files are present:**
   - `backend/render.yaml` ✅
   - `frontend/render.yaml` ✅
   - `frontend/src/config/api.js` ✅

### **Step 2: Deploy Backend (API)**

1. **Go to Render Dashboard**
2. **Click "New +" → "Web Service"**
3. **Connect GitHub repository**
4. **Configure Backend:**
   - **Name**: `vaultx-banking-api`
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/bank-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`
   - **Plan**: `Starter` (Free tier)

5. **Add Environment Variables:**
   ```
   SPRING_PROFILES_ACTIVE=prod
   JWT_SECRET=your-super-secure-jwt-secret-key-here
   ADMIN_USERNAME=admin
   ADMIN_PASSWORD=your-secure-admin-password
   FRONTEND_URL=https://vaultx-banking-frontend.onrender.com
   ```

6. **Add Database:**
   - Click "Add Database"
   - Choose "PostgreSQL"
   - Name: `vaultx-postgres`
   - Plan: `Starter` (Free tier)

7. **Update DATABASE_URL:**
   - Copy the database connection string
   - Add as environment variable: `DATABASE_URL`

8. **Deploy!**

### **Step 3: Deploy Frontend**

1. **Go to Render Dashboard**
2. **Click "New +" → "Static Site"**
3. **Connect GitHub repository**
4. **Configure Frontend:**
   - **Name**: `vaultx-banking-frontend`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist`
   - **Plan**: `Starter` (Free tier)

5. **Add Environment Variables:**
   ```
   VITE_API_URL=https://vaultx-banking-api.onrender.com/api
   ```

6. **Deploy!**

---

## 🔗 URLs After Deployment

- **Frontend**: `https://vaultx-banking-frontend.onrender.com`
- **Backend API**: `https://vaultx-banking-api.onrender.com/api`
- **Database**: Managed by Render

---

## ⚙️ Configuration Details

### **Backend Configuration**

The backend uses these Render-specific settings:

```yaml
# backend/render.yaml
services:
  - type: web
    name: vaultx-banking-api
    env: java
    plan: starter
    buildCommand: ./mvnw clean package -DskipTests
    startCommand: java -jar target/bank-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
    healthCheckPath: /api/accounts/count
```

### **Frontend Configuration**

The frontend automatically detects the environment:

```javascript
// frontend/src/config/api.js
const getApiUrl = () => {
  if (import.meta.env.PROD) {
    return 'https://vaultx-banking-api.onrender.com/api';
  } else {
    return 'http://localhost:8080/api';
  }
};
```

---

## 🛡️ Security Features

### **Automatic HTTPS**
- Render provides free SSL certificates
- All traffic is encrypted
- Secure headers configured

### **Environment Variables**
- Secrets stored securely
- Not exposed in code
- Database credentials managed

### **CORS Configuration**
- Frontend URL whitelisted
- Secure cross-origin requests
- Production-ready settings

---

## 📊 Monitoring & Logs

### **View Logs**
1. Go to your service in Render dashboard
2. Click "Logs" tab
3. View real-time logs

### **Health Checks**
- Backend: `GET /api/accounts/count`
- Automatic restart on failure
- Uptime monitoring

### **Performance**
- Free tier: 750 hours/month
- Automatic scaling
- CDN for frontend

---

## 🔄 Updates & Maintenance

### **Automatic Deployments**
- Push to `main` branch
- Automatic build and deploy
- Zero-downtime updates

### **Manual Deployments**
1. Go to service dashboard
2. Click "Manual Deploy"
3. Choose branch/commit

### **Rollback**
1. Go to "Deploys" tab
2. Click "Rollback" on previous deploy
3. Instant rollback

---

## 💰 Pricing

### **Free Tier Includes:**
- 750 hours/month per service
- PostgreSQL database
- SSL certificates
- Automatic deployments
- Basic monitoring

### **Paid Plans:**
- Starter: $7/month
- Standard: $25/month
- Pro: $85/month

---

## 🆘 Troubleshooting

### **Common Issues**

1. **Build Failures:**
   - Check build logs
   - Verify Java version (17)
   - Ensure Maven wrapper is executable

2. **Database Connection:**
   - Verify DATABASE_URL is set
   - Check database is running
   - Verify credentials

3. **Frontend Not Loading:**
   - Check if backend is running
   - Verify API URL in environment
   - Check browser console

4. **CORS Issues:**
   - Verify FRONTEND_URL matches actual URL
   - Check CORS configuration
   - Ensure HTTPS URLs

### **Debug Commands**

```bash
# Check service status
curl https://vaultx-banking-api.onrender.com/api/accounts/count

# Test frontend
curl https://vaultx-banking-frontend.onrender.com
```

---

## 🎯 Next Steps

After successful deployment:

1. **Test all functionality**
2. **Set up monitoring alerts**
3. **Configure custom domain** (optional)
4. **Set up automated backups**
5. **Monitor performance**

---

## 📞 Support

- **Render Documentation**: [render.com/docs](https://render.com/docs)
- **Community Forum**: [community.render.com](https://community.render.com)
- **Status Page**: [status.render.com](https://status.render.com)

---

## ✅ Deployment Checklist

- [ ] Code pushed to GitHub
- [ ] Backend service created
- [ ] Database added and connected
- [ ] Environment variables set
- [ ] Frontend service created
- [ ] API URL configured
- [ ] Health checks passing
- [ ] SSL certificates active
- [ ] Application accessible
- [ ] All features working

**Your VaultX Banking System is now live on Render! 🎉**
