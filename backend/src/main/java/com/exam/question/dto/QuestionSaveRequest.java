package com.exam.question.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class QuestionSaveRequest {

    private Long categoryId;

    @NotNull(message = "题型不能为空")
    @Min(value = 1, message = "题型非法")
    @Max(value = 5, message = "题型非法")
    private Integer questionType;

    @NotBlank(message = "题干不能为空")
    private String title;

    @NotNull(message = "难度不能为空")
    @Min(value = 1, message = "难度非法")
    @Max(value = 5, message = "难度非法")
    private Integer difficulty;

    @NotNull(message = "分值不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "分值必须大于0")
    private BigDecimal score;

    private String correctAnswer;

    private String answerAnalysis;

    private String knowledgePoints;

    private String tags;

    private String source;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态非法")
    @Max(value = 1, message = "状态非法")
    private Integer status;

    @Valid
    private List<QuestionOptionDTO> options;
}

