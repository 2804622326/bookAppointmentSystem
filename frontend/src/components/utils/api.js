/* global __VITE_API_BASE_URL__ */
import axios from "axios";

const FALLBACK_BASE_URL = "http://localhost:9192/api/v1";

const readConfiguredBaseUrl = () => {
  if (
    typeof __VITE_API_BASE_URL__ !== "undefined" &&
    typeof __VITE_API_BASE_URL__ === "string" &&
    __VITE_API_BASE_URL__.trim().length > 0
  ) {
    return __VITE_API_BASE_URL__.trim();
  }

  if (typeof process !== "undefined") {
    const processBaseUrl = process.env?.VITE_API_BASE_URL;
    if (processBaseUrl && processBaseUrl.trim().length > 0) {
      return processBaseUrl.trim();
    }
  }

  return undefined;
};

export const resolveApiBaseUrl = () => readConfiguredBaseUrl() ?? FALLBACK_BASE_URL;

export const api = axios.create({
  baseURL: resolveApiBaseUrl(),
});
