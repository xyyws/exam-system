package com.exam.question.service;

import com.exam.question.entity.QuestionCategory;
import java.util.List;

public interface QuestionCategoryService {
    List<QuestionCategory> listAll();
    List<QuestionCategory> listByParent(Long parentId);
    QuestionCategory getById(Long id);
    Long create(QuestionCategory category);
    void update(Long id, QuestionCategory category);
}
