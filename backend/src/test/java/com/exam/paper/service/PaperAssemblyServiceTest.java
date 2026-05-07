package com.exam.paper.service;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.security.SecurityUtils;
import com.exam.paper.entity.Paper;
import com.exam.paper.entity.PaperQuestion;
import com.exam.paper.mapper.PaperMapper;
import com.exam.paper.mapper.PaperQuestionMapper;
import com.exam.paper.mapper.PaperRuleDetailMapper;
import com.exam.paper.mapper.PaperRuleMapper;
import com.exam.question.entity.Question;
import com.exam.question.entity.QuestionOption;
import com.exam.question.mapper.QuestionMapper;
import com.exam.question.mapper.QuestionOptionMapper;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaperAssemblyServiceTest {

    @Mock
    private PaperMapper paperMapper;
    @Mock
    private PaperQuestionMapper paperQuestionMapper;
    @Mock
    private PaperRuleMapper paperRuleMapper;
    @Mock
    private PaperRuleDetailMapper ruleDetailMapper;
    @Mock
    private QuestionMapper questionMapper;
    @Mock
    private QuestionOptionMapper optionMapper;

    private PaperAssemblyService paperAssemblyService;

    @BeforeEach
    void setUp() {
        paperAssemblyService = new PaperAssemblyService(
                paperMapper, paperQuestionMapper, paperRuleMapper, ruleDetailMapper, questionMapper, optionMapper);
    }

    @Test
    void should_create_manual_paper_and_snapshots_when_questions_exist() {
        PaperAssemblyService.ManualQuestionItem item = new PaperAssemblyService.ManualQuestionItem();
        item.setQuestionId(1001L);
        item.setScore(new BigDecimal("10"));
        item.setQuestionOrder(1);

        Question question = buildQuestion(1001L, 1, "What is Java?", "A");
        QuestionOption option = buildOption(1001L, "A", "Language", 1);

        doAnswer(invocation -> {
            Paper paper = invocation.getArgument(0);
            paper.setId(501L);
            return 1;
        }).when(paperMapper).insert(any(Paper.class));
        when(questionMapper.selectById(1001L)).thenReturn(question);
        when(optionMapper.selectByQuestionId(1001L)).thenReturn(List.of(option));

        Long paperId;
        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(9L);
            paperId = paperAssemblyService.createManualPaper("Manual Paper", 90, "remark", List.of(item));
        }

        assertEquals(501L, paperId);

        ArgumentCaptor<List<PaperQuestion>> captor = ArgumentCaptor.forClass(List.class);
        verify(paperQuestionMapper).insertBatch(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertTrueContains(captor.getValue().get(0).getQuestionSnapshot(), "\"correctAnswer\":\"A\"");
        assertTrueContains(captor.getValue().get(0).getQuestionSnapshot(), "\"title\":\"What is Java?\"");
    }

    @Test
    void should_skip_missing_question_when_create_manual_paper() {
        PaperAssemblyService.ManualQuestionItem item = new PaperAssemblyService.ManualQuestionItem();
        item.setQuestionId(1001L);
        item.setScore(new BigDecimal("10"));

        doAnswer(invocation -> {
            Paper paper = invocation.getArgument(0);
            paper.setId(501L);
            return 1;
        }).when(paperMapper).insert(any(Paper.class));
        when(questionMapper.selectById(1001L)).thenReturn(null);

        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(9L);
            paperAssemblyService.createManualPaper("Manual Paper", 90, "remark", List.of(item));
        }

        ArgumentCaptor<List<PaperQuestion>> captor = ArgumentCaptor.forClass(List.class);
        verify(paperQuestionMapper).insertBatch(captor.capture());
        assertEquals(0, captor.getValue().size());
    }

    @Test
    void should_return_preview_result_when_auto_generate_preview_successful() {
        PaperAssemblyService.RuleDetailInput rule = new PaperAssemblyService.RuleDetailInput();
        rule.setQuestionType(1);
        rule.setCategoryId(10L);
        rule.setDifficulty(2);
        rule.setQuestionCount(1);
        rule.setScorePerQuestion(new BigDecimal("5"));

        Question question = buildQuestion(1001L, 1, "Question One", "A");
        QuestionOption option = buildOption(1001L, "A", "Option A", 1);

        when(questionMapper.selectForPaper(eq(1), eq(10L), eq(2), any(Set.class))).thenReturn(List.of(question));
        when(optionMapper.selectByQuestionId(1001L)).thenReturn(List.of(option));

        Map<String, Object> preview = paperAssemblyService.previewAutoPaper(List.of(rule), "Preview");

        assertEquals(new BigDecimal("5"), preview.get("totalScore"));
        assertEquals(1, preview.get("questionCount"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> questions = (List<Map<String, Object>>) preview.get("questions");
        assertEquals(1, questions.size());
        assertEquals("Question One", questions.get(0).get("title"));
    }

    @Test
    void should_throw_business_exception_when_auto_generate_inventory_insufficient() {
        PaperAssemblyService.RuleDetailInput rule = new PaperAssemblyService.RuleDetailInput();
        rule.setQuestionType(2);
        rule.setQuestionCount(2);
        rule.setDifficulty(3);

        when(questionMapper.selectForPaper(eq(2), eq(null), eq(3), any(Set.class))).thenReturn(List.of(buildQuestion(1001L, 2, "Only One", "A,B")));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> paperAssemblyService.previewAutoPaper(List.of(rule), "Preview"));

        assertEquals(ApiCodeEnum.PAPER_RULE_INSUFFICIENT.getCode(), exception.getCode());
    }

    @Test
    void should_generate_auto_paper_and_persist_questions_when_preview_disabled() {
        PaperAssemblyService.RuleDetailInput rule = new PaperAssemblyService.RuleDetailInput();
        rule.setQuestionType(1);
        rule.setQuestionCount(1);
        rule.setDifficulty(2);
        rule.setScorePerQuestion(new BigDecimal("8"));

        Question question = buildQuestion(1001L, 1, "Generated Question", "B");
        QuestionOption option = buildOption(1001L, "B", "Option B", 1);

        when(questionMapper.selectForPaper(eq(1), eq(null), eq(2), any(Set.class))).thenReturn(List.of(question));
        when(questionMapper.selectById(1001L)).thenReturn(question);
        when(optionMapper.selectByQuestionId(1001L)).thenReturn(List.of(option));
        doAnswer(invocation -> {
            Paper paper = invocation.getArgument(0);
            paper.setId(777L);
            return 1;
        }).when(paperMapper).insert(any(Paper.class));

        Long paperId;
        try (MockedStatic<SecurityUtils> mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(9L);
            paperId = paperAssemblyService.generateAutoPaper("Auto Paper", 60, "remark", List.of(rule));
        }

        assertEquals(777L, paperId);
        verify(paperMapper).insert(any(Paper.class));
        verify(paperQuestionMapper).insertBatch(any(List.class));
    }

    private Question buildQuestion(Long id, Integer type, String title, String correctAnswer) {
        Question question = new Question();
        question.setId(id);
        question.setQuestionType(type);
        question.setTitle(title);
        question.setDifficulty(2);
        question.setScore(new BigDecimal("5"));
        question.setCorrectAnswer(correctAnswer);
        question.setAnswerAnalysis("analysis");
        question.setTags("java");
        question.setStatus(1);
        return question;
    }

    private QuestionOption buildOption(Long questionId, String label, String content, Integer isCorrect) {
        QuestionOption option = new QuestionOption();
        option.setQuestionId(questionId);
        option.setOptionLabel(label);
        option.setOptionContent(content);
        option.setIsCorrect(isCorrect);
        option.setSortNo(1);
        return option;
    }

    private void assertTrueContains(String source, String target) {
        assertNotNull(source);
        assertFalse(source.isBlank());
        org.junit.jupiter.api.Assertions.assertTrue(source.contains(target));
    }
}
