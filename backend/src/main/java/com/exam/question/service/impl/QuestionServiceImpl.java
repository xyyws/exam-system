package com.exam.question.service.impl;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.security.SecurityUtils;
import com.exam.common.web.PageResponse;
import com.exam.question.dto.QuestionOptionDTO;
import com.exam.question.dto.QuestionQueryRequest;
import com.exam.question.dto.QuestionResponse;
import com.exam.question.dto.QuestionSaveRequest;
import com.exam.question.entity.Question;
import com.exam.question.entity.QuestionOption;
import com.exam.question.mapper.QuestionMapper;
import com.exam.question.mapper.QuestionOptionMapper;
import com.exam.question.service.QuestionService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;

    public QuestionServiceImpl(QuestionMapper questionMapper, QuestionOptionMapper questionOptionMapper) {
        this.questionMapper = questionMapper;
        this.questionOptionMapper = questionOptionMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQuestion(QuestionSaveRequest request) {
        validateQuestionRequest(request);
        Question question = new Question();
        BeanUtils.copyProperties(request, question);
        question.setCreatorId(SecurityUtils.getCurrentUserId());
        question.setDeleted(0);
        questionMapper.insert(question);
        saveOptions(question.getId(), request.getOptions());
        return question.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuestion(Long id, QuestionSaveRequest request) {
        validateQuestionRequest(request);
        Question existing = getExistingQuestion(id);
        Question question = new Question();
        BeanUtils.copyProperties(request, question);
        question.setId(existing.getId());
        question.setCreatorId(existing.getCreatorId());
        question.setDeleted(existing.getDeleted());
        questionMapper.updateById(question);
        questionOptionMapper.deleteByQuestionId(id);
        saveOptions(id, request.getOptions());
    }

    @Override
    public QuestionResponse getQuestionDetail(Long id) {
        Question question = getExistingQuestion(id);
        return buildResponse(question);
    }

    @Override
    public PageResponse<QuestionResponse> pageQuestions(QuestionQueryRequest request) {
        normalizePage(request);
        long total = questionMapper.countPage(request);
        List<Question> questions = total > 0 ? questionMapper.selectPage(request) : List.of();
        List<QuestionResponse> records = questions.stream().map(this::buildResponse).toList();
        return new PageResponse<>(total, request.getPageNum(), request.getPageSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestion(Long id) {
        getExistingQuestion(id);
        questionMapper.softDeleteById(id);
        questionOptionMapper.deleteByQuestionId(id);
    }

    private void validateQuestionRequest(QuestionSaveRequest request) {
        boolean needsOptions = request.getQuestionType() != null && request.getQuestionType() <= 3;
        if (needsOptions && CollectionUtils.isEmpty(request.getOptions())) {
            throw new BusinessException("B202", "当前题型需要选项");
        }
        if (request.getQuestionType() != null && request.getQuestionType() == 3
                && request.getOptions() != null && request.getOptions().size() != 2) {
            throw new BusinessException("B202", "判断题必须包含恰好两个选项");
        }
        if (!needsOptions && !CollectionUtils.isEmpty(request.getOptions())) {
            throw new BusinessException("B202", "当前题型不支持选项");
        }
    }

    private void normalizePage(QuestionQueryRequest request) {
        if (request.getPageNum() <= 0) {
            request.setPageNum(1);
        }
        if (request.getPageSize() <= 0) {
            request.setPageSize(10);
        }
    }

    private Question getExistingQuestion(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException(ApiCodeEnum.QUESTION_NOT_FOUND);
        }
        return question;
    }

    private void saveOptions(Long questionId, List<QuestionOptionDTO> optionDTOList) {
        if (CollectionUtils.isEmpty(optionDTOList)) {
            return;
        }
        List<QuestionOption> options = new ArrayList<>();
        for (QuestionOptionDTO dto : optionDTOList) {
            QuestionOption option = new QuestionOption();
            option.setQuestionId(questionId);
            option.setOptionLabel(dto.getOptionLabel());
            option.setOptionContent(dto.getOptionContent());
            option.setIsCorrect(dto.getIsCorrect());
            option.setSortNo(dto.getSortNo() == null ? 0 : dto.getSortNo());
            options.add(option);
        }
        questionOptionMapper.batchInsert(options);
    }

    private QuestionResponse buildResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        BeanUtils.copyProperties(question, response);
        List<QuestionOption> options = questionOptionMapper.selectByQuestionId(question.getId());
        response.setOptions(options.stream().map(this::toOptionDTO).toList());
        return response;
    }

    private QuestionOptionDTO toOptionDTO(QuestionOption option) {
        QuestionOptionDTO dto = new QuestionOptionDTO();
        dto.setId(option.getId());
        dto.setOptionLabel(option.getOptionLabel());
        dto.setOptionContent(option.getOptionContent());
        dto.setIsCorrect(option.getIsCorrect());
        dto.setSortNo(option.getSortNo());
        return dto;
    }
}
