// API Configuration for different environments
const getApiUrl = () => {
  // Check if we're in production (Vite uses import.meta.env)
  const isProduction = import.meta.env.PROD || import.meta.env.MODE === 'production';
  
  if (isProduction) {
    // Production API URL
    return import.meta.env.VITE_API_URL || 'https://vaultx-banking-api.onrender.com/api';
  } else {
    // Development API URL
    return 'http://localhost:8080/api';
  }
};

export const API_BASE_URL = getApiUrl();

// Helper function to create full API URLs
export const createApiUrl = (endpoint) => {
  return `${API_BASE_URL}${endpoint.startsWith('/') ? endpoint : `/${endpoint}`}`;
};
