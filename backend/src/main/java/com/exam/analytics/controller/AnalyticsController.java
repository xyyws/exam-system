package com.exam.analytics.controller;

import com.exam.analytics.entity.WrongQuestionBook;
import com.exam.analytics.mapper.WrongQuestionBookMapper;
import com.exam.common.security.SecurityUtils;
import com.exam.common.web.ApiResponse;
import com.exam.exam.entity.Exam;
import com.exam.exam.mapper.ExamMapper;
import com.exam.exam.mapper.ExamPaperMapper;
import com.exam.question.mapper.QuestionMapper;
import com.exam.runtime.mapper.ExamAnswerMapper;
import org.springframework.http.ResponseEntity;
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
    private final ExamMapper examMapper;
    private final WrongQuestionBookMapper wrongQuestionBookMapper;
    private final QuestionMapper questionMapper;

    public AnalyticsController(ExamAnswerMapper answerMapper, ExamPaperMapper examPaperMapper,
                               ExamMapper examMapper, WrongQuestionBookMapper wrongQuestionBookMapper,
                               QuestionMapper questionMapper) {
        this.answerMapper = answerMapper;
        this.examPaperMapper = examPaperMapper;
        this.examMapper = examMapper;
        this.wrongQuestionBookMapper = wrongQuestionBookMapper;
        this.questionMapper = questionMapper;
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
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            BigDecimal score = (BigDecimal) row.get("total_score");
            BigDecimal max = (BigDecimal) row.get("total_max");
            Long examId = ((Number) row.get("exam_id")).longValue();
            var exam = examMapper.selectById(examId);
            BigDecimal passScore = exam != null ? exam.getPassScore() : BigDecimal.valueOf(60);
            item.put("examId", examId);
            item.put("examPaperId", row.get("examPaperId"));
            item.put("examName", row.get("exam_name"));
            item.put("totalScore", score);
            item.put("totalMax", max);
            item.put("percentage", max != null && max.compareTo(BigDecimal.ZERO) > 0
                    ? score.multiply(BigDecimal.valueOf(100)).divide(max, 1, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ZERO);
            item.put("passFlag", score != null && passScore != null && score.compareTo(passScore) >= 0);
            item.put("submitTime", row.get("start_time"));
            result.add(item);
        }
        return ApiResponse.success(result);
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

    @PutMapping("/api/student/analytics/wrong-questions/{questionId}/note")
    public ApiResponse<Void> updateNote(@PathVariable Long questionId, @RequestBody Map<String, String> body) {
        Long studentId = SecurityUtils.getCurrentUserId();
        wrongQuestionBookMapper.updateNote(studentId, questionId, body.getOrDefault("noteText", ""));
        return ApiResponse.success();
    }

    // ─── Teacher: 题目正确率分析 ───
    @GetMapping("/api/teacher/analytics/exams/{examId}/question-accuracy")
    public ApiResponse<List<Map<String, Object>>> questionAccuracy(@PathVariable Long examId) {
        List<Map<String, Object>> rows = answerMapper.selectQuestionAccuracy(examId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Long qId = ((Number) row.get("question_id")).longValue();
            var q = questionMapper.selectById(qId);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("questionId", qId);
            item.put("questionTitle", q != null ? q.getTitle() : "题目已删除");
            item.put("questionType", QUESTION_TYPE_MAP.getOrDefault((Integer) row.get("question_type"), "未知"));
            item.put("totalCount", row.get("total_count"));
            item.put("correctCount", row.get("correct_count"));
            item.put("accuracy", row.get("accuracy"));
            result.add(item);
        }
        return ApiResponse.success(result);
    }

    // ─── Teacher: 成绩导出 CSV ───
    @GetMapping("/api/teacher/analytics/exams/{examId}/export")
    public ResponseEntity<byte[]> exportScores(@PathVariable Long examId) throws Exception {
        List<Map<String, Object>> records = examPaperMapper.selectExamRankings(examId, 0, 10000);

        var sb = new StringBuilder();
        sb.append("排名,学生姓名,学号,总分,状态\r\n");
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> r = records.get(i);
            sb.append(i + 1).append(',')
              .append(r.get("studentName")).append(',')
              .append(r.get("studentNo")).append(',')
              .append(r.get("totalScore")).append(',')
              .append(r.get("answerStatus") != null && ((Number) r.get("answerStatus")).intValue() >= 3 ? "已判分" : "已交卷")
              .append("\r\n");
        }

        byte[] data = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv;charset=UTF-8")
                .header("Content-Disposition", "attachment;filename=scores_exam_" + examId + ".csv")
                .contentLength(data.length)
                .body(data);
    }
}
