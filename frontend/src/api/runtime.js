import request from "./request";

export const getAvailableExams = () => request.get("/student/exams/available");
export const getOngoingExam = () => request.get("/student/exams/ongoing");
export const enterExam = (examId, deviceInfo) => request.post(`/student/exams/${examId}/enter`, { deviceInfo });
export const saveAnswer = (examId, data) => request.post(`/student/exams/${examId}/answers/save`, data);
export const reportCutScreen = (examId, data) => request.post(`/student/exams/${examId}/anti-cheat/cut-screen`, data);
export const submitExam = (examId, data) => request.post(`/student/exams/${examId}/submit`, data);
