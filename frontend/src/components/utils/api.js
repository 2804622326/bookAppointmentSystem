import axios from "axios";

// Determine the base URL for API requests. The value can be overridden
// using the `REACT_APP_API_BASE_URL` environment variable; otherwise, it falls
// back to the ALB endpoint for production deployment.
const baseURL = process.env.REACT_APP_API_BASE_URL || "http://alb-pet-384183543.eu-west-1.elb.amazonaws.com/api/v1";

export const api = axios.create({
  baseURL,
});