import request from "./request";

// Users
export const getUsers = (params) => request.get("/admin/users", { params });
export const getStudents = (params) => request.get("/admin/users", { params: { ...params, userType: 3, pageSize: 200 } });
export const getUser = (id) => request.get(`/admin/users/${id}`);
export const createUser = (data) => request.post("/admin/users", data);
export const updateUser = (id, data) => request.put(`/admin/users/${id}`, data);
export const updateUserStatus = (id, status) => request.put(`/admin/users/${id}/status`, { status });
export const assignUserRoles = (id, roleIds) => request.put(`/admin/users/${id}/roles`, { roleIds });
export const resetPassword = (id, newPassword) => request.post(`/admin/users/${id}/reset-password`, { newPassword });
export const deleteUser = (id) => request.delete(`/admin/users/${id}`);

// Roles
export const getRoles = () => request.get("/admin/roles");
export const createRole = (data) => request.post("/admin/roles", data);
export const updateRole = (id, data) => request.put(`/admin/roles/${id}`, data);

// Classes
export const getClasses = (params) => request.get("/admin/classes", { params });
export const createClass = (data) => request.post("/admin/classes", data);
export const updateClass = (id, data) => request.put(`/admin/classes/${id}`, data);
export const deleteClass = (id) => request.delete(`/admin/classes/${id}`);

// Operation Logs
export const getLogs = (params) => request.get("/admin/logs", { params });
