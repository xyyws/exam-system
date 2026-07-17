package com.exam.marking.controller;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.enums.QuestionTypeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.security.SecurityUtils;
import com.exam.common.web.ApiResponse;
import com.exam.exam.entity.ExamPaper;
import com.exam.exam.mapper.ExamPaperMapper;
import com.exam.paper.entity.PaperQuestion;
import com.exam.paper.mapper.PaperQuestionMapper;
import com.exam.runtime.entity.ExamAnswer;
import com.exam.runtime.mapper.ExamAnswerMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher/marking")
public class MarkingController {

    private final ExamAnswerMapper answerMapper;
    private final ExamPaperMapper examPaperMapper;
    private final PaperQuestionMapper paperQuestionMapper;

    public MarkingController(ExamAnswerMapper answerMapper, ExamPaperMapper examPaperMapper,
                             PaperQuestionMapper paperQuestionMapper) {
        this.answerMapper = answerMapper;
        this.examPaperMapper = examPaperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
    }

    @GetMapping("/exam-papers/{examPaperId}")
    public ApiResponse<Map<String, Object>> getExamPaperDetail(@PathVariable Long examPaperId) {
        var allAnswers = answerMapper.selectByExamPaperId(examPaperId);
        // Only return subjective answers for marking
        var subjective = allAnswers.stream()
                .filter(a -> QuestionTypeEnum.isSubjective(a.getQuestionType()))
                .toList();

        // Build questionId -> snapshot title map from paper_question
        ExamPaper ep = examPaperMapper.selectById(examPaperId);
        Map<Long, String> questionTitles = new HashMap<>();
        if (ep != null) {
            List<PaperQuestion> pqs = paperQuestionMapper.selectByPaperId(ep.getPaperId());
            for (PaperQuestion pq : pqs) {
                try {
                    var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    var node = mapper.readTree(pq.getQuestionSnapshot());
                    questionTitles.put(pq.getQuestionId(), node.has("title") ? node.get("title").asText() : "");
                } catch (Exception ignored) {}
            }
        }

        // Enrich answers with question title
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (ExamAnswer a : subjective) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("examPaperId", a.getExamPaperId());
            item.put("questionId", a.getQuestionId());
            item.put("questionType", a.getQuestionType());
            item.put("studentAnswer", a.getStudentAnswer());
            item.put("score", a.getScore());
            item.put("maxScore", a.getMaxScore());
            item.put("markStatus", a.getMarkStatus());
            item.put("markComment", a.getMarkComment());
            item.put("questionTitle", questionTitles.getOrDefault(a.getQuestionId(), "题目已删除"));
            enriched.add(item);
        }
        return ApiResponse.success(Map.of("examPaperId", examPaperId, "answers", enriched));
    }

    @PostMapping("/answers/{answerId}/score")
    @Transactional
    public ApiResponse<Void> scoreAnswer(@PathVariable Long answerId, @RequestBody Map<String, Object> body) {
        ExamAnswer answer = answerMapper.selectById(answerId);
        if (answer == null)
            throw new BusinessException(ApiCodeEnum.MARKING_TASK_NOT_FOUND);

        // CRITICAL FIX: only score subjective, unmarked answers
        if (!QuestionTypeEnum.isSubjective(answer.getQuestionType()))
            throw new BusinessException("B502", "仅主观题支持人工评分");
        if (answer.getMarkStatus() == 1)
            throw new BusinessException("B503", "该题已阅卷");

        Long teacherId = SecurityUtils.getCurrentUserId();
        answer.setScore(new BigDecimal(body.get("score").toString()));
        answer.setMarkComment((String) body.getOrDefault("markComment", ""));
        answer.setMarkStatus(1);
        answer.setAnswerStatus(2);
        answer.setMarkTeacherId(teacherId);
        answer.setMarkTime(LocalDateTime.now());
        answerMapper.updateScore(answer);

        // Recalculate and close out exam paper scores
        recalcExamPaperScore(answer.getExamPaperId());
        return ApiResponse.success();
    }

    private void recalcExamPaperScore(Long examPaperId) {
        var answers = answerMapper.selectByExamPaperId(examPaperId);
        BigDecimal objScore = answers.stream()
                .filter(a -> QuestionTypeEnum.isObjective(a.getQuestionType()))
                .map(a -> a.getScore() != null ? a.getScore() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal subjScore = answers.stream()
                .filter(a -> QuestionTypeEnum.isSubjective(a.getQuestionType()))
                .map(a -> a.getScore() != null ? a.getScore() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean allMarked = answers.stream().noneMatch(a -> a.getMarkStatus() == 0);

        ExamPaper ep = examPaperMapper.selectById(examPaperId);
        if (ep != null) {
            ep.setObjectiveScore(objScore);
            ep.setSubjectiveScore(subjScore);
            ep.setTotalScore(objScore.add(subjScore));
            if (allMarked) {
                ep.setAnswerStatus(3); // SCORED
            }
            examPaperMapper.update(ep);
        }
    }
}
