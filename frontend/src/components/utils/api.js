import axios from "axios";

// Determine the base URL for API requests. The value can be overridden
// using the `VITE_API_BASE_URL` environment variable; otherwise, it falls
// back to the local development server.
const baseURL = process.env.VITE_API_BASE_URL || "http://54.228.78.4:9192/api/v1";

export const api = axios.create({
  baseURL,
});
