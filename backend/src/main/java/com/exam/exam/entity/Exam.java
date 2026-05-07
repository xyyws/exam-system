package com.exam.exam.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Exam {
    private Long id;
    private String examName;
    private Long paperId;
    private Integer examMode; // 1固定 2随机题序 3随机选项序
    private Integer publishStatus; // 0草稿 1已发布 2已结束 3已归档
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private BigDecimal totalScore;
    private BigDecimal passScore;
    private Integer antiCheatSwitch;
    private Integer cutScreenLimit;
    private Integer autoSubmitSwitch;
    private Integer allowRepeatEnter;
    private Long creatorId;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String paperName; // JOIN paper table

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }
    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }
    public Integer getExamMode() { return examMode; }
    public void setExamMode(Integer examMode) { this.examMode = examMode; }
    public Integer getPublishStatus() { return publishStatus; }
    public void setPublishStatus(Integer publishStatus) { this.publishStatus = publishStatus; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }
    public BigDecimal getPassScore() { return passScore; }
    public void setPassScore(BigDecimal passScore) { this.passScore = passScore; }
    public Integer getAntiCheatSwitch() { return antiCheatSwitch; }
    public void setAntiCheatSwitch(Integer antiCheatSwitch) { this.antiCheatSwitch = antiCheatSwitch; }
    public Integer getCutScreenLimit() { return cutScreenLimit; }
    public void setCutScreenLimit(Integer cutScreenLimit) { this.cutScreenLimit = cutScreenLimit; }
    public Integer getAutoSubmitSwitch() { return autoSubmitSwitch; }
    public void setAutoSubmitSwitch(Integer autoSubmitSwitch) { this.autoSubmitSwitch = autoSubmitSwitch; }
    public Integer getAllowRepeatEnter() { return allowRepeatEnter; }
    public void setAllowRepeatEnter(Integer allowRepeatEnter) { this.allowRepeatEnter = allowRepeatEnter; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public String getPaperName() { return paperName; }
    public void setPaperName(String paperName) { this.paperName = paperName; }
}
