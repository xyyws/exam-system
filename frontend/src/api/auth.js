import request from "./request";

export function login(data) {
  return request.post("/auth/login", data);
}

export function logout() {
  return request.post("/auth/logout");
}

export function getCurrentUser() {
  return request.get("/auth/me");
}

export function refreshToken(refreshToken) {
  return request.post("/auth/refresh", { refreshToken });
}

export function changePassword(data) {
  return request.put("/auth/password", data);
}
