import request from "./request";

export const getExamPaperDetail = (examPaperId) => request.get(`/teacher/marking/exam-papers/${examPaperId}`);
export const scoreAnswer = (answerId, data) => request.post(`/teacher/marking/answers/${answerId}/score`, data);
