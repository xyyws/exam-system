package com.exam.question.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class QuestionOption {

    private Long id;
    private Long questionId;
    private String optionLabel;
    private String optionContent;
    private Integer isCorrect;
    private Integer sortNo;
    private LocalDateTime createTime;
}

