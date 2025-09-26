import axios from "axios";

export const FALLBACK_API_BASE_URL = "http://localhost:9192/api/v1";

const reactApiBaseUrl =
  typeof process !== "undefined" ? process.env?.REACT_APP_API_BASE_URL : undefined;

export const API_BASE_URL = reactApiBaseUrl ?? FALLBACK_API_BASE_URL;

export const api = axios.create({
  baseURL: API_BASE_URL,
});
