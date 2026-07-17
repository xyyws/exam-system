import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: "http://localhost:8081/api",
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
    // Blob 响应（文件下载）直接放行
    if (response.config.responseType === "blob") return response;
    const { code, message } = response.data;
    if (code === "0") return response.data;
    ElMessage.error(message || "请求失败");
    return Promise.reject(new Error(message));
  },
  async (error) => {
    if (error.response) {
      const status = error.response.status;
      // blob 响应里的 JSON 错误需要先转文本解析
      let msg = "服务器错误";
      if (error.response.data instanceof Blob) {
        try { const text = await error.response.data.text(); msg = JSON.parse(text).message || msg; } catch {}
      } else {
        msg = error.response.data?.message || msg;
      }
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
