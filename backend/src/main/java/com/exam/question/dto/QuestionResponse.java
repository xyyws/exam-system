package com.exam.question.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class QuestionResponse {

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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<QuestionOptionDTO> options;
}

