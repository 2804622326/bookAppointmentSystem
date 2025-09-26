import axios from "axios";

// Support both development and production environments
const getBaseURL = () => {
  // In production (Docker), use environment variable
  if (process.env.REACT_APP_API_BASE_URL) {
    return process.env.REACT_APP_API_BASE_URL;
  }
  
  // In development, use localhost
  return "http://localhost:9192/api/v1";
};

export const api = axios.create({
  baseURL: getBaseURL(),
});
