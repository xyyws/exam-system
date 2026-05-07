package com.exam.runtime.service;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.enums.ExamPaperStatusEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.security.SecurityUtils;
import com.exam.exam.entity.Exam;
import com.exam.exam.entity.ExamPaper;
import com.exam.exam.mapper.ExamMapper;
import com.exam.exam.mapper.ExamPaperMapper;
import com.exam.paper.entity.PaperQuestion;
import com.exam.paper.mapper.PaperQuestionMapper;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuntimeServiceTest {

    @Mock
    private ExamMapper examMapper;
    @Mock
    private ExamPaperMapper examPaperMapper;
    @Mock
    private PaperQuestionMapper paperQuestionMapper;
    @Mock
    private ExamAnswerMapper answerMapper;

    private RuntimeService runtimeService;

    @BeforeEach
    void setUp() {
        runtimeService = new RuntimeService(examMapper, examPaperMapper, paperQuestionMapper, answerMapper);
    }

    @Test
    void should_initialize_answers_and_strip_sensitive_fields_when_enter_exam_first_time() {
        Exam exam = buildExam(11L, 21L);
        ExamPaper examPaper = buildExamPaper(31L, 11L, 41L, ExamPaperStatusEnum.NOT_STARTED.getCode());
        PaperQuestion paperQuestion = buildPaperQuestion(101L, 1, 1, new BigDecimal("5"), "{\"title\":\"Q1\",\"difficulty\":2,\"options\":[{\"label\":\"A\"}],\"tags\":\"tag1\",\"correctAnswer\":\"A\",\"answerAnalysis\":\"secret\"}");

        when(examMapper.selectById(11L)).thenReturn(exam);
        when(examPaperMapper.selectByExamAndStudent(11L, 41L)).thenReturn(examPaper);
        when(paperQuestionMapper.selectByPaperId(21L)).thenReturn(List.of(paperQuestion));
        when(answerMapper.selectByExamPaperId(31L)).thenReturn(List.of());

        Map<String, Object> result = runtimeService.enterExam(11L, 41L, "Chrome");

        ArgumentCaptor<ExamPaper> examPaperCaptor = ArgumentCaptor.forClass(ExamPaper.class);
        verify(examPaperMapper).update(examPaperCaptor.capture());
        assertEquals(ExamPaperStatusEnum.IN_PROGRESS.getCode(), examPaperCaptor.getValue().getAnswerStatus());
        assertEquals("Chrome", examPaperCaptor.getValue().getDeviceInfo());
        assertNotNull(examPaperCaptor.getValue().getStartTime());

        ArgumentCaptor<List<ExamAnswer>> answersCaptor = ArgumentCaptor.forClass(List.class);
        verify(answerMapper).insertBatch(answersCaptor.capture());
        assertEquals(1, answersCaptor.getValue().size());
        assertEquals("A", answersCaptor.getValue().get(0).getCorrectAnswer());

        List<Map<String, Object>> questions = castQuestionList(result.get("questions"));
        assertEquals(1, questions.size());
        assertEquals("Q1", questions.get(0).get("title"));
        assertFalse(questions.get(0).containsKey("correctAnswer"));
        assertFalse(questions.get(0).containsKey("answerAnalysis"));
    }

    @Test
    void should_throw_exception_when_student_not_assigned_to_exam() {
        Exam exam = buildExam(11L, 21L);
        when(examMapper.selectById(11L)).thenReturn(exam);
        when(examPaperMapper.selectByExamAndStudent(11L, 41L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> runtimeService.enterExam(11L, 41L, "Chrome"));

        assertEquals(ApiCodeEnum.EXAM_STUDENT_NOT_ASSIGNED.getCode(), exception.getCode());
    }

    @Test
    void should_update_matching_answer_when_save_answer() {
        ExamPaper examPaper = buildExamPaper(31L, 11L, 41L, ExamPaperStatusEnum.IN_PROGRESS.getCode());
        ExamAnswer answer = buildAnswer(501L, 31L, 701L, 41L, 1, "A", new BigDecimal("5"));

        when(examPaperMapper.selectById(31L)).thenReturn(examPaper);
        when(answerMapper.selectByExamPaperId(31L)).thenReturn(List.of(answer));

        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(41L);

            runtimeService.saveAnswer(11L, 31L, 701L, "B");
        }

        ArgumentCaptor<ExamAnswer> answerCaptor = ArgumentCaptor.forClass(ExamAnswer.class);
        verify(answerMapper).updateAnswer(answerCaptor.capture());
        assertEquals("B", answerCaptor.getValue().getStudentAnswer());
        assertEquals(1, answerCaptor.getValue().getAnswerStatus());
    }

    @Test
    void should_force_submit_when_cut_screen_reaches_limit() {
        ExamPaper examPaper = buildExamPaper(31L, 11L, 41L, ExamPaperStatusEnum.IN_PROGRESS.getCode());
        examPaper.setCutScreenCount(2);
        examPaper.setViolationCount(0);

        Exam exam = buildExam(11L, 21L);
        exam.setAntiCheatSwitch(1);
        exam.setCutScreenLimit(3);
        exam.setAutoSubmitSwitch(1);

        when(examPaperMapper.selectById(31L)).thenReturn(examPaper);
        when(examMapper.selectById(11L)).thenReturn(exam);

        Map<String, Object> result;
        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(41L);
            result = runtimeService.reportCutScreen(11L, 31L);
        }

        assertEquals(3, result.get("cutScreenCount"));
        assertEquals(1, result.get("violationCount"));
        assertEquals(true, result.get("forceSubmit"));
    }

    @Test
    void should_auto_score_objective_answers_and_mark_exam_paper_scored_when_submit_exam() {
        ExamPaper examPaper = buildExamPaper(31L, 11L, 41L, ExamPaperStatusEnum.IN_PROGRESS.getCode());
        examPaper.setStartTime(LocalDateTime.now().minusMinutes(20));

        ExamAnswer singleChoice = buildAnswer(501L, 31L, 701L, 41L, 1, "A", new BigDecimal("5"));
        singleChoice.setStudentAnswer("A");
        ExamAnswer multiChoice = buildAnswer(502L, 31L, 702L, 41L, 2, "A,C", new BigDecimal("10"));
        multiChoice.setStudentAnswer("C,A");

        when(examPaperMapper.selectById(31L)).thenReturn(examPaper, examPaper);
        when(answerMapper.selectByExamPaperId(31L)).thenReturn(List.of(singleChoice, multiChoice));

        Map<String, Object> result;
        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(41L);
            result = runtimeService.submitExam(11L, 31L, false);
        }

        assertEquals("submitted", result.get("scoreStatus"));
        verify(answerMapper, times(2)).updateScore(any(ExamAnswer.class));

        ArgumentCaptor<ExamPaper> examPaperCaptor = ArgumentCaptor.forClass(ExamPaper.class);
        verify(examPaperMapper, times(2)).update(examPaperCaptor.capture());
        ExamPaper scoredPaper = examPaperCaptor.getAllValues().get(1);
        assertEquals(new BigDecimal("15"), scoredPaper.getObjectiveScore());
        assertEquals(new BigDecimal("15"), scoredPaper.getTotalScore());
        assertEquals(ExamPaperStatusEnum.SCORED.getCode(), scoredPaper.getAnswerStatus());
    }

    @Test
    void should_throw_forbidden_when_exam_paper_belongs_to_another_student() {
        ExamPaper examPaper = buildExamPaper(31L, 11L, 99L, ExamPaperStatusEnum.IN_PROGRESS.getCode());
        when(examPaperMapper.selectById(31L)).thenReturn(examPaper);

        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(41L);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> runtimeService.submitExam(11L, 31L, false));

            assertEquals(ApiCodeEnum.FORBIDDEN.getCode(), exception.getCode());
        }
    }

    private Exam buildExam(Long examId, Long paperId) {
        Exam exam = new Exam();
        exam.setId(examId);
        exam.setPaperId(paperId);
        exam.setExamName("Java Midterm");
        exam.setPublishStatus(1);
        exam.setStartTime(LocalDateTime.now().minusMinutes(30));
        exam.setEndTime(LocalDateTime.now().plusMinutes(30));
        exam.setDurationMinutes(60);
        exam.setTotalScore(new BigDecimal("100"));
        exam.setPassScore(new BigDecimal("60"));
        exam.setAntiCheatSwitch(0);
        exam.setCutScreenLimit(3);
        exam.setAutoSubmitSwitch(0);
        return exam;
    }

    private ExamPaper buildExamPaper(Long examPaperId, Long examId, Long studentId, Integer status) {
        ExamPaper examPaper = new ExamPaper();
        examPaper.setId(examPaperId);
        examPaper.setExamId(examId);
        examPaper.setPaperId(21L);
        examPaper.setStudentId(studentId);
        examPaper.setAnswerStatus(status);
        examPaper.setCutScreenCount(0);
        examPaper.setViolationCount(0);
        examPaper.setObjectiveScore(BigDecimal.ZERO);
        examPaper.setSubjectiveScore(BigDecimal.ZERO);
        examPaper.setTotalScore(BigDecimal.ZERO);
        examPaper.setAutoSubmit(0);
        return examPaper;
    }

    private PaperQuestion buildPaperQuestion(Long questionId, Integer type, Integer order, BigDecimal score, String snapshot) {
        PaperQuestion paperQuestion = new PaperQuestion();
        paperQuestion.setQuestionId(questionId);
        paperQuestion.setQuestionType(type);
        paperQuestion.setQuestionOrder(order);
        paperQuestion.setScore(score);
        paperQuestion.setQuestionSnapshot(snapshot);
        return paperQuestion;
    }

    private ExamAnswer buildAnswer(Long id, Long examPaperId, Long questionId, Long studentId, Integer type, String correctAnswer, BigDecimal maxScore) {
        ExamAnswer answer = new ExamAnswer();
        answer.setId(id);
        answer.setExamPaperId(examPaperId);
        answer.setQuestionId(questionId);
        answer.setStudentId(studentId);
        answer.setQuestionType(type);
        answer.setCorrectAnswer(correctAnswer);
        answer.setMaxScore(maxScore);
        answer.setScore(BigDecimal.ZERO);
        answer.setMarkStatus(0);
        answer.setAnswerStatus(1);
        return answer;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> castQuestionList(Object value) {
        assertInstanceOf(List.class, value);
        return (List<Map<String, Object>>) value;
    }
}
