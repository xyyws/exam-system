package com.exam.runtime.service;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.enums.ExamPaperStatusEnum;
import com.exam.common.enums.QuestionTypeEnum;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RuntimeService {

    private final ExamMapper examMapper;
    private final ExamPaperMapper examPaperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final ExamAnswerMapper answerMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RuntimeService(ExamMapper examMapper, ExamPaperMapper examPaperMapper,
                          PaperQuestionMapper paperQuestionMapper, ExamAnswerMapper answerMapper) {
        this.examMapper = examMapper;
        this.examPaperMapper = examPaperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.answerMapper = answerMapper;
    }

    public Map<String, Object> getAvailableExams(Long studentId) {
        List<Exam> exams = examMapper.selectAvailable(studentId, LocalDateTime.now().toString());
        List<Map<String, Object>> list = new ArrayList<>();
        for (Exam e : exams) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("examId", e.getId());
            item.put("examName", e.getExamName());
            item.put("durationMinutes", e.getDurationMinutes());
            item.put("totalScore", e.getTotalScore());
            item.put("passScore", e.getPassScore());
            item.put("startTime", e.getStartTime().toString());
            item.put("endTime", e.getEndTime().toString());
            list.add(item);
        }
        return Map.of("exams", list);
    }

    public Map<String, Object> getOngoingExam(Long studentId) {
        Map<String, Object> ongoing = examPaperMapper.selectOngoingByStudentId(studentId);
        if (ongoing == null) {
            return Map.of("hasOngoing", false);
        }
        LocalDateTime endTime = ((java.sql.Timestamp) ongoing.get("end_time")).toLocalDateTime();
        long remainSeconds = endTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                - System.currentTimeMillis();
        remainSeconds = Math.max(0, remainSeconds / 1000);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("hasOngoing", true);
        result.put("examPaperId", ongoing.get("exam_paper_id"));
        result.put("examId", ongoing.get("exam_id"));
        result.put("examName", ongoing.get("exam_name"));
        result.put("remainSeconds", remainSeconds);
        result.put("answeredCount", ongoing.get("answered_count"));
        result.put("totalCount", ongoing.get("total_count"));
        return result;
    }

    @Transactional
    public Map<String, Object> enterExam(Long examId, Long studentId, String deviceInfo) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null || exam.getPublishStatus() != 1)
            throw new BusinessException(ApiCodeEnum.EXAM_NOT_PUBLISHED);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime()) || now.isAfter(exam.getEndTime()))
            throw new BusinessException(ApiCodeEnum.EXAM_TIME_INVALID);

        // CRITICAL FIX: verify student is assigned to this exam
        ExamPaper ep = examPaperMapper.selectByExamAndStudent(examId, studentId);
        if (ep == null) {
            throw new BusinessException(ApiCodeEnum.EXAM_STUDENT_NOT_ASSIGNED);
        }
        if (ep.getAnswerStatus() == 2 || ep.getAnswerStatus() == 3)
            throw new BusinessException(ApiCodeEnum.EXAM_ALREADY_SUBMITTED);
        if (ep.getAnswerStatus() == 1)
            return buildRuntimePaper(ep, exam);

        // First entry: initialize answer records
        ep.setAnswerStatus(ExamPaperStatusEnum.IN_PROGRESS.getCode());
        ep.setStartTime(now);
        ep.setDeviceInfo(deviceInfo);
        examPaperMapper.update(ep);

        List<PaperQuestion> pqs = paperQuestionMapper.selectByPaperId(exam.getPaperId());
        List<ExamAnswer> answers = new ArrayList<>();
        for (PaperQuestion pq : pqs) {
            ExamAnswer ans = new ExamAnswer();
            ans.setExamPaperId(ep.getId());
            ans.setExamId(examId);
            ans.setPaperId(exam.getPaperId());
            ans.setQuestionId(pq.getQuestionId());
            ans.setStudentId(studentId);
            ans.setQuestionType(pq.getQuestionType());
            ans.setQuestionOrder(pq.getQuestionOrder());
            ans.setMaxScore(pq.getScore());
            ans.setScore(BigDecimal.ZERO);
            ans.setAnswerStatus(0);
            ans.setMarkStatus(0);
            ans.setCorrectAnswer(extractCorrectAnswerFromSnapshot(pq.getQuestionSnapshot()));
            answers.add(ans);
        }
        answerMapper.insertBatch(answers);
        return buildRuntimePaper(ep, exam);
    }

    // CRITICAL FIX: strip sensitive data from student view
    private Map<String, Object> buildRuntimePaper(ExamPaper ep, Exam exam) {
        List<PaperQuestion> pqs = paperQuestionMapper.selectByPaperId(exam.getPaperId());
        List<ExamAnswer> answers = answerMapper.selectByExamPaperId(ep.getId());

        long remainSeconds = exam.getEndTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                - System.currentTimeMillis();
        remainSeconds = Math.max(0, remainSeconds / 1000);

        // Strip correctAnswer from paper questions for student view
        List<Map<String, Object>> sanitizedQuestions = new ArrayList<>();
        for (PaperQuestion pq : pqs) {
            Map<String, Object> q = new LinkedHashMap<>();
            q.put("questionId", pq.getQuestionId());
            q.put("questionType", pq.getQuestionType());
            q.put("questionOrder", pq.getQuestionOrder());
            q.put("score", pq.getScore());
            try {
                Map<String, Object> snap = objectMapper.readValue(pq.getQuestionSnapshot(), new TypeReference<>() {});
                q.put("title", snap.get("title"));
                q.put("difficulty", snap.get("difficulty"));
                q.put("options", snap.get("options"));
                q.put("tags", snap.get("tags"));
                // deliberately exclude: correctAnswer, answerAnalysis
            } catch (Exception e) {
                q.put("title", "解析错误");
            }
            sanitizedQuestions.add(q);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("examPaperId", ep.getId());
        result.put("examId", exam.getId());
        result.put("examName", exam.getExamName());
        result.put("durationMinutes", exam.getDurationMinutes());
        result.put("totalScore", exam.getTotalScore());
        result.put("remainSeconds", remainSeconds);
        result.put("questions", sanitizedQuestions);
        result.put("answers", answers);
        return result;
    }

    // CRITICAL FIX: verify ownership before any write operation
    private ExamPaper getOwnedExamPaper(Long examPaperId, Long examId) {
        Long studentId = SecurityUtils.getCurrentUserId();
        ExamPaper ep = examPaperMapper.selectById(examPaperId);
        if (ep == null)
            throw new BusinessException(ApiCodeEnum.SYSTEM_ERROR.getCode(), "答卷不存在");
        if (!ep.getStudentId().equals(studentId))
            throw new BusinessException(ApiCodeEnum.FORBIDDEN);
        if (!ep.getExamId().equals(examId))
            throw new BusinessException(ApiCodeEnum.SYSTEM_ERROR.getCode(), "考试不匹配");
        return ep;
    }

    public void saveAnswer(Long examId, Long examPaperId, Long questionId, String studentAnswer) {
        getOwnedExamPaper(examPaperId, examId);
        List<ExamAnswer> answers = answerMapper.selectByExamPaperId(examPaperId);
        for (ExamAnswer a : answers) {
            if (a.getQuestionId().equals(questionId)) {
                a.setStudentAnswer(studentAnswer);
                a.setAnswerStatus(1);
                answerMapper.updateAnswer(a);
                return;
            }
        }
    }

    public Map<String, Object> reportCutScreen(Long examId, Long examPaperId) {
        ExamPaper ep = getOwnedExamPaper(examPaperId, examId);
        ep.setCutScreenCount(ep.getCutScreenCount() + 1);
        Exam exam = examMapper.selectById(ep.getExamId());
        boolean forceSubmit = false;
        if (exam.getAntiCheatSwitch() == 1 && ep.getCutScreenCount() >= exam.getCutScreenLimit()) {
            ep.setViolationCount(ep.getViolationCount() + 1);
            if (exam.getAutoSubmitSwitch() == 1) {
                forceSubmit = true;
            }
        }
        examPaperMapper.update(ep);
        return Map.of("cutScreenCount", ep.getCutScreenCount(), "violationCount", ep.getViolationCount(), "forceSubmit", forceSubmit);
    }

    @Transactional
    public Map<String, Object> submitExam(Long examId, Long examPaperId, boolean autoSubmit) {
        ExamPaper ep = getOwnedExamPaper(examPaperId, examId);
        if (ep.getAnswerStatus() >= 2)
            throw new BusinessException(ApiCodeEnum.EXAM_ALREADY_SUBMITTED);

        ep.setAnswerStatus(ExamPaperStatusEnum.SUBMITTED.getCode());
        ep.setSubmitTime(LocalDateTime.now());
        if (autoSubmit) ep.setAutoSubmit(1);
        if (ep.getStartTime() != null) {
            ep.setUsedMinutes((int) java.time.Duration.between(ep.getStartTime(), LocalDateTime.now()).toMinutes());
        }
        examPaperMapper.update(ep);

        autoScoreObjective(ep.getId());
        return Map.of("scoreStatus", "submitted");
    }

    public void autoScoreObjective(Long examPaperId) {
        List<ExamAnswer> answers = answerMapper.selectByExamPaperId(examPaperId);
        BigDecimal objectiveTotal = BigDecimal.ZERO;
        for (ExamAnswer a : answers) {
            if (QuestionTypeEnum.isSubjective(a.getQuestionType())) continue;
            boolean correct = judgeObjective(a.getStudentAnswer(), a.getCorrectAnswer(), a.getQuestionType());
            a.setIsCorrect(correct ? 1 : 0);
            a.setScore(correct ? a.getMaxScore() : BigDecimal.ZERO);
            a.setAnswerStatus(2);
            objectiveTotal = objectiveTotal.add(a.getScore());
            answerMapper.updateScore(a);
        }
        ExamPaper ep = examPaperMapper.selectById(examPaperId);
        ep.setObjectiveScore(objectiveTotal);
        ep.setTotalScore(objectiveTotal);
        boolean hasSubjective = answers.stream().anyMatch(a -> QuestionTypeEnum.isSubjective(a.getQuestionType()));
        if (!hasSubjective) {
            ep.setTotalScore(objectiveTotal);
            ep.setAnswerStatus(ExamPaperStatusEnum.SCORED.getCode());
        }
        examPaperMapper.update(ep);
    }

    private boolean judgeObjective(String studentAnswer, String correctAnswer, Integer questionType) {
        if (studentAnswer == null || correctAnswer == null) return false;
        String sa = studentAnswer.trim().toUpperCase();
        String ca = correctAnswer.trim().toUpperCase();
        if (questionType == 1 || questionType == 3) return sa.equals(ca);
        if (questionType == 2) return sortAnswer(sa).equals(sortAnswer(ca));
        return sa.equals(ca);
    }

    private String sortAnswer(String answer) {
        if (answer == null) return "";
        char[] chars = answer.replaceAll("[,\\\\s]", "").toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    private String extractCorrectAnswerFromSnapshot(String snapshot) {
        try {
            Map<String, Object> map = objectMapper.readValue(snapshot, new TypeReference<>() {});
            Object ca = map.get("correctAnswer");
            return ca != null ? ca.toString() : null;
        } catch (Exception e) { return null; }
    }
}
