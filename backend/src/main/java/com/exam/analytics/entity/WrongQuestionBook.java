package com.exam.analytics.entity;

import java.time.LocalDateTime;

public class WrongQuestionBook {
    private Long id;
    private Long studentId;
    private Long questionId;
    private Long examId;
    private Long examPaperId;
    private LocalDateTime firstWrongTime;
    private LocalDateTime lastWrongTime;
    private Integer wrongCount;
    private String latestAnswer;
    private Integer masteredStatus;
    private LocalDateTime masteredTime;
    private String noteText;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Joined fields
    private String questionTitle;
    private Integer questionType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public Long getExamPaperId() { return examPaperId; }
    public void setExamPaperId(Long examPaperId) { this.examPaperId = examPaperId; }
    public LocalDateTime getFirstWrongTime() { return firstWrongTime; }
    public void setFirstWrongTime(LocalDateTime firstWrongTime) { this.firstWrongTime = firstWrongTime; }
    public LocalDateTime getLastWrongTime() { return lastWrongTime; }
    public void setLastWrongTime(LocalDateTime lastWrongTime) { this.lastWrongTime = lastWrongTime; }
    public Integer getWrongCount() { return wrongCount; }
    public void setWrongCount(Integer wrongCount) { this.wrongCount = wrongCount; }
    public String getLatestAnswer() { return latestAnswer; }
    public void setLatestAnswer(String latestAnswer) { this.latestAnswer = latestAnswer; }
    public Integer getMasteredStatus() { return masteredStatus; }
    public void setMasteredStatus(Integer masteredStatus) { this.masteredStatus = masteredStatus; }
    public LocalDateTime getMasteredTime() { return masteredTime; }
    public void setMasteredTime(LocalDateTime masteredTime) { this.masteredTime = masteredTime; }
    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public String getQuestionTitle() { return questionTitle; }
    public void setQuestionTitle(String questionTitle) { this.questionTitle = questionTitle; }
    public Integer getQuestionType() { return questionType; }
    public void setQuestionType(Integer questionType) { this.questionType = questionType; }
}
