package com.exam.question.service;

import com.exam.common.web.PageResponse;
import com.exam.question.dto.QuestionQueryRequest;
import com.exam.question.dto.QuestionResponse;
import com.exam.question.dto.QuestionSaveRequest;

public interface QuestionService {

    Long createQuestion(QuestionSaveRequest request);

    void updateQuestion(Long id, QuestionSaveRequest request);

    QuestionResponse getQuestionDetail(Long id);

    PageResponse<QuestionResponse> pageQuestions(QuestionQueryRequest request);

    void deleteQuestion(Long id);
}

