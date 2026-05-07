package com.exam.marking.controller;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.security.SecurityUtils;
import com.exam.common.web.ApiResponse;
import com.exam.exam.entity.ExamPaper;
import com.exam.exam.mapper.ExamPaperMapper;
import com.exam.runtime.entity.ExamAnswer;
import com.exam.runtime.mapper.ExamAnswerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarkingControllerTest {

    @Mock
    private ExamAnswerMapper answerMapper;
    @Mock
    private ExamPaperMapper examPaperMapper;

    private MarkingController markingController;

    @BeforeEach
    void setUp() {
        markingController = new MarkingController(answerMapper, examPaperMapper);
    }

    @Test
    void should_return_only_subjective_answers_when_get_exam_paper_detail() {
        ExamAnswer objective = buildAnswer(1L, 99L, 2, new BigDecimal("5"), 1, 0);
        ExamAnswer subjective = buildAnswer(2L, 99L, 5, null, 1, 0);

        when(answerMapper.selectByExamPaperId(99L)).thenReturn(List.of(objective, subjective));

        ApiResponse<Map<String, Object>> response = markingController.getExamPaperDetail(99L);

        @SuppressWarnings("unchecked")
        List<ExamAnswer> answers = (List<ExamAnswer>) response.getData().get("answers");
        assertEquals(1, answers.size());
        assertEquals(5, answers.get(0).getQuestionType());
    }

    @Test
    void should_throw_exception_when_score_answer_not_found() {
        when(answerMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> markingController.scoreAnswer(1L, Map.of("score", "8")));

        assertEquals(ApiCodeEnum.MARKING_TASK_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void should_throw_exception_when_scoring_objective_answer() {
        ExamAnswer answer = buildAnswer(1L, 99L, 1, new BigDecimal("5"), 1, 0);
        when(answerMapper.selectById(1L)).thenReturn(answer);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> markingController.scoreAnswer(1L, Map.of("score", "8")));

        assertEquals("B502", exception.getCode());
    }

    @Test
    void should_throw_exception_when_answer_already_marked() {
        ExamAnswer answer = buildAnswer(1L, 99L, 5, new BigDecimal("5"), 1, 1);
        when(answerMapper.selectById(1L)).thenReturn(answer);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> markingController.scoreAnswer(1L, Map.of("score", "8")));

        assertEquals("B503", exception.getCode());
    }

    @Test
    void should_update_answer_and_recalculate_exam_paper_when_score_subjective_answer() {
        ExamAnswer answer = buildAnswer(1L, 99L, 5, null, 1, 0);
        ExamAnswer objective = buildAnswer(2L, 99L, 2, new BigDecimal("10"), 2, 1);
        ExamAnswer subjective = buildAnswer(1L, 99L, 5, new BigDecimal("8"), 1, 1);
        ExamPaper examPaper = new ExamPaper();
        examPaper.setId(99L);

        when(answerMapper.selectById(1L)).thenReturn(answer);
        when(answerMapper.selectByExamPaperId(99L)).thenReturn(List.of(objective, subjective));
        when(examPaperMapper.selectById(99L)).thenReturn(examPaper);

        ApiResponse<Void> response;
        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(66L);
            response = markingController.scoreAnswer(1L, Map.of("score", "8", "markComment", "good"));
        }

        assertNotNull(response);
        ArgumentCaptor<ExamAnswer> answerCaptor = ArgumentCaptor.forClass(ExamAnswer.class);
        verify(answerMapper).updateScore(answerCaptor.capture());
        assertEquals(new BigDecimal("8"), answerCaptor.getValue().getScore());
        assertEquals(66L, answerCaptor.getValue().getMarkTeacherId());
        assertEquals(1, answerCaptor.getValue().getMarkStatus());
        assertNotNull(answerCaptor.getValue().getMarkTime());

        ArgumentCaptor<ExamPaper> examPaperCaptor = ArgumentCaptor.forClass(ExamPaper.class);
        verify(examPaperMapper).update(examPaperCaptor.capture());
        assertEquals(new BigDecimal("10"), examPaperCaptor.getValue().getObjectiveScore());
        assertEquals(new BigDecimal("8"), examPaperCaptor.getValue().getSubjectiveScore());
        assertEquals(new BigDecimal("18"), examPaperCaptor.getValue().getTotalScore());
        assertEquals(3, examPaperCaptor.getValue().getAnswerStatus());
    }

    private ExamAnswer buildAnswer(Long id, Long examPaperId, Integer questionType, BigDecimal score, Integer answerStatus, Integer markStatus) {
        ExamAnswer answer = new ExamAnswer();
        answer.setId(id);
        answer.setExamPaperId(examPaperId);
        answer.setQuestionType(questionType);
        answer.setScore(score);
        answer.setAnswerStatus(answerStatus);
        answer.setMarkStatus(markStatus);
        return answer;
    }
}

