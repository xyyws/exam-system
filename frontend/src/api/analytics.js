import request from "./request";

export function getScoreTrend() {
  return request.get("/student/analytics/trend");
}

export function getTypeBreakdown() {
  return request.get("/student/analytics/breakdown");
}

export function getWrongBookSummary() {
  return request.get("/student/analytics/wrong-book/summary");
}

export function getQuestionAccuracy(examId) {
  return request.get(`/teacher/analytics/exams/${examId}/question-accuracy`);
}
