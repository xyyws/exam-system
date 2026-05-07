package com.exam.runtime.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExamAnswer {
    private Long id;
    private Long examPaperId;
    private Long examId;
    private Long paperId;
    private Long questionId;
    private Long studentId;
    private Integer questionType;
    private Integer questionOrder;
    private String studentAnswer;
    private String correctAnswer;
    private Integer isCorrect;
    private BigDecimal score;
    private BigDecimal maxScore;
    private Integer answerStatus; // 0未答 1已答 2已判分
    private Integer markStatus;  // 0未阅 1已阅
    private Long markTeacherId;
    private LocalDateTime markTime;
    private String markComment;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getExamPaperId() { return examPaperId; }
    public void setExamPaperId(Long examPaperId) { this.examPaperId = examPaperId; }
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Integer getQuestionType() { return questionType; }
    public void setQuestionType(Integer questionType) { this.questionType = questionType; }
    public Integer getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }
    public String getStudentAnswer() { return studentAnswer; }
    public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public Integer getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Integer isCorrect) { this.isCorrect = isCorrect; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public BigDecimal getMaxScore() { return maxScore; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }
    public Integer getAnswerStatus() { return answerStatus; }
    public void setAnswerStatus(Integer answerStatus) { this.answerStatus = answerStatus; }
    public Integer getMarkStatus() { return markStatus; }
    public void setMarkStatus(Integer markStatus) { this.markStatus = markStatus; }
    public Long getMarkTeacherId() { return markTeacherId; }
    public void setMarkTeacherId(Long markTeacherId) { this.markTeacherId = markTeacherId; }
    public LocalDateTime getMarkTime() { return markTime; }
    public void setMarkTime(LocalDateTime markTime) { this.markTime = markTime; }
    public String getMarkComment() { return markComment; }
    public void setMarkComment(String markComment) { this.markComment = markComment; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
