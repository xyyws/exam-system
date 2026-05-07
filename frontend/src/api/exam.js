import request from "./request";

export const getExams = (params) => request.get("/teacher/exams", { params });
export const getExam = (id) => request.get(`/teacher/exams/${id}`);
export const createExam = (data) => request.post("/teacher/exams", data);
export const publishExam = (id) => request.post(`/teacher/exams/${id}/publish`);
export const closeExam = (id) => request.post(`/teacher/exams/${id}/close`);
