package com.exam.question.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Question {

    private Long id;
    private Long categoryId;
    private Integer questionType;
    private String title;
    private Integer difficulty;
    private BigDecimal score;
    private String correctAnswer;
    private String answerAnalysis;
    private String knowledgePoints;
    private String tags;
    private String source;
    private Integer status;
    private Long creatorId;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

