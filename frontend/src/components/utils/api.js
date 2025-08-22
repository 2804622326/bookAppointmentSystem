import axios from "axios";

const baseURL =
  import.meta.env.VITE_API_BASE_URL ||
  "https://bookApp-1543210322.eu-west-1.elb.amazonaws.com/api";

export const api = axios.create({
  baseURL,
});
