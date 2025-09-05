// API Configuration for different environments
const getApiUrl = () => {
  // Check if we're in production (Create React App uses process.env)
  const isProduction = process.env.NODE_ENV === 'production';
  
  if (isProduction) {
    // Production API URL
    return process.env.REACT_APP_API_URL || 'https://vaultx-banking-api.onrender.com/api';
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
