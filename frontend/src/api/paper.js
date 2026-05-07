import request from "./request";

export const getPapers = (params) => request.get("/teacher/papers", { params });
export const createManualPaper = (data) => request.post("/teacher/papers/manual", data);
export const autoGeneratePaper = (data) => request.post("/teacher/papers/auto-generate", data);
export const deletePaper = (id) => request.delete(`/teacher/papers/${id}`);
