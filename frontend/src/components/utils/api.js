import axios from "axios";

export const api = axios.create({
  baseURL: "http://34.246.135.101:9192/api/v1",
});
