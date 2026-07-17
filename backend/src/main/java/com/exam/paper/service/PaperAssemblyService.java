package com.exam.paper.service;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.security.SecurityUtils;
import com.exam.paper.entity.*;
import com.exam.paper.mapper.*;
import com.exam.question.entity.Question;
import com.exam.question.entity.QuestionOption;
import com.exam.question.mapper.QuestionMapper;
import com.exam.question.mapper.QuestionOptionMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaperAssemblyService {

    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final PaperRuleMapper paperRuleMapper;
    private final PaperRuleDetailMapper ruleDetailMapper;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper optionMapper;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public PaperAssemblyService(PaperMapper paperMapper, PaperQuestionMapper paperQuestionMapper,
                                PaperRuleMapper paperRuleMapper, PaperRuleDetailMapper ruleDetailMapper,
                                QuestionMapper questionMapper, QuestionOptionMapper optionMapper) {
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.paperRuleMapper = paperRuleMapper;
        this.ruleDetailMapper = ruleDetailMapper;
        this.questionMapper = questionMapper;
        this.optionMapper = optionMapper;
    }

    /**
     * Manual paper: teacher selects specific questions with scores and ordering.
     */
    @Transactional
    public Long createManualPaper(String paperName, Integer durationMinutes, String remark,
                                   List<ManualQuestionItem> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException(ApiCodeEnum.BAD_REQUEST.getCode(), "试卷至少需要选择1道题目");
        }

        Paper paper = new Paper();
        paper.setPaperName(paperName);
        paper.setPaperType(1);
        paper.setDurationMinutes(durationMinutes == null ? 60 : durationMinutes);
        paper.setRemark(remark);
        paper.setQuestionCount(items.size());
        paper.setStatus(1);
        paper.setDeleted(0);
        paper.setCreatorId(SecurityUtils.getCurrentUserId());

        BigDecimal totalScore = BigDecimal.ZERO;
        for (ManualQuestionItem item : items) {
            BigDecimal s = item.getScore() != null ? item.getScore() : BigDecimal.ZERO;
            totalScore = totalScore.add(s);
        }
        paper.setTotalScore(totalScore);
        paperMapper.insert(paper);

        List<PaperQuestion> pqList = new ArrayList<>();
        int order = 1;
        for (ManualQuestionItem item : items) {
            Question q = questionMapper.selectById(item.getQuestionId());
            if (q == null) continue;
            List<QuestionOption> options = optionMapper.selectByQuestionId(q.getId());
            BigDecimal score = item.getScore() != null ? item.getScore() : q.getScore();
            if (score == null) score = new BigDecimal("5");
            PaperQuestion pq = new PaperQuestion();
            pq.setPaperId(paper.getId());
            pq.setQuestionId(q.getId());
            pq.setQuestionType(q.getQuestionType());
            pq.setQuestionOrder(item.getQuestionOrder() != null ? item.getQuestionOrder() : order);
            pq.setScore(score);
            pq.setQuestionSnapshot(buildSnapshot(q, options, score));
            pqList.add(pq);
            order++;
        }

        if (pqList.isEmpty()) {
            throw new BusinessException(ApiCodeEnum.BAD_REQUEST.getCode(), "所选题目均无效，请重新选择");
        }
        paperQuestionMapper.insertBatch(pqList);
        return paper.getId();
    }

    /**
     * Auto paper generation: rule-based random selection with fallback.
     */
    @Transactional
    public Map<String, Object> previewAutoPaper(List<RuleDetailInput> rules, String paperName) {
        return doGenerate(rules, paperName, null, true);
    }

    @Transactional
    public Long generateAutoPaper(String paperName, Integer durationMinutes, String remark,
                                   List<RuleDetailInput> rules) {
        Paper paper = new Paper();
        paper.setPaperName(paperName);
        paper.setPaperType(2);
        paper.setDurationMinutes(durationMinutes == null ? 60 : durationMinutes);
        paper.setRemark(remark);
        paper.setStatus(1);
        paper.setCreatorId(SecurityUtils.getCurrentUserId());

        Map<String, Object> result = doGenerate(rules, paperName, paper, false);
        return paper.getId();
    }

    private Map<String, Object> doGenerate(List<RuleDetailInput> rules, String paperName,
                                            Paper paper, boolean previewOnly) {
        Set<Long> selectedIds = new HashSet<>();
        List<Map<String, Object>> picked = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;

        for (RuleDetailInput rule : rules) {
            List<Question> candidates = questionMapper.selectForPaper(
                    rule.getQuestionType(), rule.getCategoryId(), rule.getDifficulty(), selectedIds);
            if (candidates.size() < rule.getQuestionCount()) {
                throw new BusinessException(ApiCodeEnum.PAPER_RULE_INSUFFICIENT.getCode(),
                        String.format("%s缺%d道可使用", rule.getQuestionType() == 1 ? "单选题" : "其他题型",
                                rule.getQuestionCount() - candidates.size()));
            }
            Collections.shuffle(candidates);
            List<Question> chosen = candidates.subList(0, rule.getQuestionCount());
            for (Question q : chosen) {
                selectedIds.add(q.getId());
                List<QuestionOption> options = optionMapper.selectByQuestionId(q.getId());
                BigDecimal score = rule.getScorePerQuestion() != null ? rule.getScorePerQuestion() : q.getScore();
                totalScore = totalScore.add(score);
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("questionId", q.getId());
                item.put("questionType", q.getQuestionType());
                item.put("title", q.getTitle());
                item.put("difficulty", q.getDifficulty());
                item.put("score", score);
                item.put("options", options);
                item.put("correctAnswer", q.getCorrectAnswer());
                picked.add(item);
            }
        }

        if (!previewOnly && paper != null) {
            paper.setQuestionCount(picked.size());
            paper.setTotalScore(totalScore);
            paperMapper.insert(paper);

            List<PaperQuestion> pqList = new ArrayList<>();
            int order = 1;
            for (Map<String, Object> item : picked) {
                Long qid = (Long) item.get("questionId");
                Question q = questionMapper.selectById(qid);
                List<QuestionOption> options = optionMapper.selectByQuestionId(qid);
                PaperQuestion pq = new PaperQuestion();
                pq.setPaperId(paper.getId());
                pq.setQuestionId(qid);
                pq.setQuestionType((Integer) item.get("questionType"));
                pq.setQuestionOrder(order++);
                pq.setScore((BigDecimal) item.get("score"));
                pq.setQuestionSnapshot(buildSnapshot(q, options, (BigDecimal) item.get("score")));
                pqList.add(pq);
            }
            paperQuestionMapper.insertBatch(pqList);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalScore", totalScore);
        result.put("questionCount", picked.size());
        result.put("questions", picked);
        return result;
    }

    private String buildSnapshot(Question q, List<QuestionOption> options, BigDecimal score) {
        Map<String, Object> snap = new LinkedHashMap<>();
        snap.put("sourceQuestionId", q.getId());
        snap.put("versionTime", LocalDateTime.now().toString());
        snap.put("questionType", q.getQuestionType());
        snap.put("title", q.getTitle());
        snap.put("difficulty", q.getDifficulty());
        snap.put("score", score);
        snap.put("correctAnswer", q.getCorrectAnswer());
        snap.put("analysis", q.getAnswerAnalysis());
        snap.put("options", options);
        snap.put("tags", q.getTags());
        try {
            return objectMapper.writeValueAsString(snap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("快照序列化失败", e);
        }
    }

    // Inner classes for manual / auto paper input
    public static class ManualQuestionItem {
        private Long questionId;
        private BigDecimal score;
        private Integer questionOrder;

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }
        public Integer getQuestionOrder() { return questionOrder; }
        public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }
    }

    public static class RuleDetailInput {
        private Integer questionType;
        private Long categoryId;
        private Integer difficulty;
        private Integer questionCount;
        private BigDecimal scorePerQuestion;

        public Integer getQuestionType() { return questionType; }
        public void setQuestionType(Integer questionType) { this.questionType = questionType; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public Integer getDifficulty() { return difficulty; }
        public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public Integer getQuestionCount() { return questionCount; }
        public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
        public BigDecimal getScorePerQuestion() { return scorePerQuestion; }
        public void setScorePerQuestion(BigDecimal scorePerQuestion) { this.scorePerQuestion = scorePerQuestion; }
    }
}
