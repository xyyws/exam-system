package com.exam.exam.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExamPaper {
    private Long id;
    private Long examId;
    private Long paperId;
    private Long studentId;
    private Integer attemptNo;
    private Integer answerStatus; // 0未开始 1进行中 2已交卷 3已判分
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private Integer autoSubmit;
    private Integer usedMinutes;
    private BigDecimal objectiveScore;
    private BigDecimal subjectiveScore;
    private BigDecimal totalScore;
    private Integer cutScreenCount;
    private Integer violationCount;
    private String ipAddress;
    private String deviceInfo;
    private Integer extraMinutes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Integer getAttemptNo() { return attemptNo; }
    public void setAttemptNo(Integer attemptNo) { this.attemptNo = attemptNo; }
    public Integer getAnswerStatus() { return answerStatus; }
    public void setAnswerStatus(Integer answerStatus) { this.answerStatus = answerStatus; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public Integer getAutoSubmit() { return autoSubmit; }
    public void setAutoSubmit(Integer autoSubmit) { this.autoSubmit = autoSubmit; }
    public Integer getUsedMinutes() { return usedMinutes; }
    public void setUsedMinutes(Integer usedMinutes) { this.usedMinutes = usedMinutes; }
    public BigDecimal getObjectiveScore() { return objectiveScore; }
    public void setObjectiveScore(BigDecimal objectiveScore) { this.objectiveScore = objectiveScore; }
    public BigDecimal getSubjectiveScore() { return subjectiveScore; }
    public void setSubjectiveScore(BigDecimal subjectiveScore) { this.subjectiveScore = subjectiveScore; }
    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }
    public Integer getCutScreenCount() { return cutScreenCount; }
    public void setCutScreenCount(Integer cutScreenCount) { this.cutScreenCount = cutScreenCount; }
    public Integer getViolationCount() { return violationCount; }
    public void setViolationCount(Integer violationCount) { this.violationCount = violationCount; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    public Integer getExtraMinutes() { return extraMinutes; }
    public void setExtraMinutes(Integer extraMinutes) { this.extraMinutes = extraMinutes; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
