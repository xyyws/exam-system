import request from "./request";

export const getExams = (params) => request.get("/teacher/exams", { params });
export const getExam = (id) => request.get(`/teacher/exams/${id}`);
export const createExam = (data) => request.post("/teacher/exams", data);
export const updateExam = (id, data) => request.put(`/teacher/exams/${id}`, data);
export const publishExam = (id) => request.post(`/teacher/exams/${id}/publish`);
export const closeExam = (id) => request.post(`/teacher/exams/${id}/close`);
export const extendTime = (examId, data) => request.post(`/teacher/exams/${examId}/extend-time`, data);
export const assignStudents = (examId, studentIds) => request.post(`/teacher/exams/${examId}/assign-students`, { studentIds });
export const getAssignedStudents = (examId) => request.get(`/teacher/exams/${examId}/assigned-students`);
export const getExamStudents = (params) => request.get("/teacher/exams/students", { params });
export const getTeacherClasses = () => request.get("/teacher/exams/classes");
