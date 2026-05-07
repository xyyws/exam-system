package com.exam.paper.entity;

public class PaperRuleDetail {
    private Long id;
    private Long ruleId;
    private Integer questionType;
    private Long categoryId;
    private Integer difficulty;
    private Integer questionCount;
    private java.math.BigDecimal scorePerQuestion;
    private Integer sortNo;
    private java.time.LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
    public Integer getQuestionType() { return questionType; }
    public void setQuestionType(Integer questionType) { this.questionType = questionType; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    public java.math.BigDecimal getScorePerQuestion() { return scorePerQuestion; }
    public void setScorePerQuestion(java.math.BigDecimal scorePerQuestion) { this.scorePerQuestion = scorePerQuestion; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public java.time.LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(java.time.LocalDateTime createTime) { this.createTime = createTime; }
}
