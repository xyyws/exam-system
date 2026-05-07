import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 15000
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (response) => {
    const { code, message } = response.data;
    if (code === "0") return response.data;
    ElMessage.error(message || "请求失败");
    return Promise.reject(new Error(message));
  },
  (error) => {
    if (error.response) {
      const status = error.response.status;
      const msg = error.response.data?.message || "服务器错误";
      if (status === 401) {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        // Use SPA redirect instead of full page reload
        const path = window.location.pathname + window.location.search;
        if (!path.startsWith("/login")) {
          window.location.href = "/login?redirect=" + encodeURIComponent(path);
        }
        return Promise.reject(error);
      }
      if (status === 403) {
        ElMessage.error("无权限访问");
      } else {
        ElMessage.error(msg);
      }
    } else {
      ElMessage.error("网络错误");
    }
    return Promise.reject(error);
  }
);

export default request;
