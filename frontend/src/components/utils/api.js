import axios from "axios";

// Determine the base URL for API requests. The value can be overridden
// using the `REACT_APP_API_BASE_URL` environment variable; otherwise, it falls
// back to the ALB endpoint for production deployment.
const baseURL = process.env.REACT_APP_API_BASE_URL || "http://alb-pet-384183543.eu-west-1.elb.amazonaws.com/api/v1";

export const api = axios.create({
  baseURL,
});

// Add request interceptor to automatically include auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("authToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token might be expired or invalid
      localStorage.removeItem("authToken");
      localStorage.removeItem("userRole");
      localStorage.removeItem("userId");
      
      // Redirect to login page
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);