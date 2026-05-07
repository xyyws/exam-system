import request from "./request";

export const getQuestions = (params) => request.get("/teacher/questions", { params });
export const getQuestion = (id) => request.get(`/teacher/questions/${id}`);
export const createQuestion = (data) => request.post("/teacher/questions", data);
export const updateQuestion = (id, data) => request.put(`/teacher/questions/${id}`, data);
export const deleteQuestion = (id) => request.delete(`/teacher/questions/${id}`);

export const getCategories = (parentId) => request.get("/teacher/question-categories", { params: { parentId } });
export const createCategory = (data) => request.post("/teacher/question-categories", data);
export const updateCategory = (id, data) => request.put(`/teacher/question-categories/${id}`, data);

// 批量导入
export const importQuestions = (formData) => request.post("/teacher/questions/import", formData, {
  headers: { "Content-Type": "multipart/form-data" }
});
export const downloadImportTemplate = () => request.get("/teacher/questions/import/template", { responseType: "blob" });
