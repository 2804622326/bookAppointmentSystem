import axios from "axios";

// Determine the base URL for API requests. The value can be overridden
// using the `REACT_APP_API_BASE_URL` environment variable; otherwise, it falls
// back to the local development server.
const baseURL = process.env.REACT_APP_API_BASE_URL || "http://localhost:9193/api/v1";

export const api = axios.create({
  baseURL,
});