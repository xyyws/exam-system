package com.exam.question.service.impl;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.question.entity.QuestionCategory;
import com.exam.question.mapper.QuestionCategoryMapper;
import com.exam.question.service.QuestionCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionCategoryServiceImpl implements QuestionCategoryService {

    private final QuestionCategoryMapper mapper;

    public QuestionCategoryServiceImpl(QuestionCategoryMapper mapper) { this.mapper = mapper; }

    @Override
    public List<QuestionCategory> listAll() { return mapper.selectAll(); }

    @Override
    public List<QuestionCategory> listByParent(Long parentId) { return mapper.selectByParentId(parentId); }

    @Override
    public QuestionCategory getById(Long id) {
        QuestionCategory c = mapper.selectById(id);
        if (c == null) throw new BusinessException("B203", "题目分类不存在");
        return c;
    }

    @Override
    public Long create(QuestionCategory category) {
        mapper.insert(category);
        return category.getId();
    }

    @Override
    public void update(Long id, QuestionCategory category) {
        QuestionCategory exist = mapper.selectById(id);
        if (exist == null) throw new BusinessException("B203", "题目分类不存在");
        category.setId(id);
        mapper.update(category);
    }
}
