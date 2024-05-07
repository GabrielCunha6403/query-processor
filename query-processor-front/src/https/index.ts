import axios, { type AxiosInstance } from "axios";

export const QS: AxiosInstance = axios.create({
//    baseURL: "/negociacao-back/app", 
  baseURL: "http://localhost:8080/qp-api",
  timeout: 200000000000,
  withCredentials: true,
  headers: {
    "content-type": "application/json",
    accept: "application/json",
  },
});
