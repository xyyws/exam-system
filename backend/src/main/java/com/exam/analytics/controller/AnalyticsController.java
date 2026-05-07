package com.exam.analytics.controller;

import com.exam.analytics.entity.WrongQuestionBook;
import com.exam.analytics.mapper.WrongQuestionBookMapper;
import com.exam.common.security.SecurityUtils;
import com.exam.common.web.ApiResponse;
import com.exam.exam.mapper.ExamPaperMapper;
import com.exam.runtime.mapper.ExamAnswerMapper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AnalyticsController {

    private static final Map<Integer, String> QUESTION_TYPE_MAP = Map.of(
            1, "单选题", 2, "多选题", 3, "判断题", 4, "填空题", 5, "简答题"
    );

    private final ExamAnswerMapper answerMapper;
    private final ExamPaperMapper examPaperMapper;
    private final WrongQuestionBookMapper wrongQuestionBookMapper;

    public AnalyticsController(ExamAnswerMapper answerMapper, ExamPaperMapper examPaperMapper,
                               WrongQuestionBookMapper wrongQuestionBookMapper) {
        this.answerMapper = answerMapper;
        this.examPaperMapper = examPaperMapper;
        this.wrongQuestionBookMapper = wrongQuestionBookMapper;
    }

    @GetMapping("/api/student/analytics/trend")
    public ApiResponse<List<Map<String, Object>>> scoreTrend() {
        Long studentId = SecurityUtils.getCurrentUserId();
        List<Map<String, Object>> rows = answerMapper.selectScoreTrend(studentId);

        List<Map<String, Object>> result = rows.stream().map(row -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("examId", row.get("exam_id"));
            item.put("examName", row.get("exam_name"));
            item.put("totalScore", row.get("total_score"));
            item.put("totalMax", row.get("total_max"));
            item.put("examTime", row.get("start_time"));

            BigDecimal score = (BigDecimal) row.get("total_score");
            BigDecimal max = (BigDecimal) row.get("total_max");
            BigDecimal pct = BigDecimal.ZERO;
            if (max != null && max.compareTo(BigDecimal.ZERO) > 0) {
                pct = score.multiply(BigDecimal.valueOf(100))
                        .divide(max, 1, RoundingMode.HALF_UP);
            }
            item.put("percentage", pct);
            return item;
        }).collect(Collectors.toList());

        return ApiResponse.success(result);
    }

    @GetMapping("/api/student/analytics/breakdown")
    public ApiResponse<Map<String, Object>> typeBreakdown() {
        Long studentId = SecurityUtils.getCurrentUserId();
        List<Map<String, Object>> rows = answerMapper.selectTypeBreakdown(studentId);

        List<String> types = new ArrayList<>();
        List<BigDecimal> correctRates = new ArrayList<>();
        int totalCorrect = 0, totalCount = 0;

        for (Map<String, Object> row : rows) {
            Integer qt = (Integer) row.get("question_type");
            String label = QUESTION_TYPE_MAP.getOrDefault(qt, "题型" + qt);
            types.add(label);

            Number cc = (Number) row.get("correct_count");
            Number tc = (Number) row.get("total_count");
            totalCorrect += cc.intValue();
            totalCount += tc.intValue();

            BigDecimal rate = tc.intValue() > 0
                    ? BigDecimal.valueOf(cc.doubleValue() / tc.doubleValue() * 100)
                        .setScale(1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            correctRates.add(rate);
        }

        BigDecimal overallRate = totalCount > 0
                ? BigDecimal.valueOf((double) totalCorrect / totalCount * 100)
                        .setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("types", types);
        result.put("correctRates", correctRates);
        result.put("totalCorrect", totalCorrect);
        result.put("totalCount", totalCount);
        result.put("overallRate", overallRate);
        result.put("rows", rows);

        return ApiResponse.success(result);
    }

    // Teacher analytics
    @GetMapping("/api/teacher/analytics/exams/{examId}/summary")
    public ApiResponse<Map<String, Object>> examSummary(@PathVariable Long examId) {
        Map<String, Object> stats = examPaperMapper.selectExamStats(examId);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("examId", examId);
        summary.put("participantCount", stats.getOrDefault("participant_count", 0));
        summary.put("submitCount", stats.getOrDefault("submit_count", 0));
        summary.put("avgScore", stats.getOrDefault("avg_score", "0"));
        summary.put("maxScore", stats.getOrDefault("max_score", "0"));
        summary.put("minScore", stats.getOrDefault("min_score", "0"));
        summary.put("passRate", stats.getOrDefault("pass_rate", "0"));
        return ApiResponse.success(summary);
    }

    @GetMapping("/api/teacher/analytics/exams/{examId}/rankings")
    public ApiResponse<Map<String, Object>> rankings(@PathVariable Long examId,
                                                      @RequestParam(defaultValue = "1") int pageNum,
                                                      @RequestParam(defaultValue = "50") int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Map<String, Object>> records = examPaperMapper.selectExamRankings(examId, offset, pageSize);
        long total = examPaperMapper.countExamParticipants(examId);
        return ApiResponse.success(Map.of("records", records, "total", total));
    }

    @GetMapping("/api/student/analytics/scores")
    public ApiResponse<List<Map<String, Object>>> studentScores() {
        Long studentId = SecurityUtils.getCurrentUserId();
        List<Map<String, Object>> rows = answerMapper.selectScoreTrend(studentId);
        return ApiResponse.success(rows);
    }

    @GetMapping("/api/student/analytics/wrong-book/summary")
    public ApiResponse<Map<String, Object>> wrongBookSummary() {
        Long studentId = SecurityUtils.getCurrentUserId();
        List<Map<String, Object>> rows = wrongQuestionBookMapper.selectSummaryByStudent(studentId);
        int total = 0;
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Integer qt = (Integer) row.get("questionType");
            Number cnt = (Number) row.get("count");
            total += cnt.intValue();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("questionType", qt);
            item.put("label", QUESTION_TYPE_MAP.getOrDefault(qt, "题型" + qt));
            item.put("count", cnt.intValue());
            items.add(item);
        }
        return ApiResponse.success(Map.of("total", total, "items", items));
    }

    @GetMapping("/api/student/analytics/wrong-questions")
    public ApiResponse<Map<String, Object>> wrongQuestions(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long studentId = SecurityUtils.getCurrentUserId();
        int offset = (pageNum - 1) * pageSize;
        List<WrongQuestionBook> records = wrongQuestionBookMapper.selectByStudent(studentId, offset, pageSize);
        long total = wrongQuestionBookMapper.countByStudent(studentId);
        return ApiResponse.success(Map.of("records", records, "total", total,
                "pageNum", pageNum, "pageSize", pageSize));
    }

    @PutMapping("/api/student/analytics/wrong-questions/{questionId}/mastered")
    public ApiResponse<Void> toggleMastered(@PathVariable Long questionId, @RequestBody Map<String, Object> body) {
        Long studentId = SecurityUtils.getCurrentUserId();
        Integer masteredStatus = (Integer) body.getOrDefault("masteredStatus", 0);
        wrongQuestionBookMapper.updateMasteredByStudentAndQuestion(studentId, questionId, masteredStatus);
        return ApiResponse.success();
    }
}
