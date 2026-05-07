package com.exam.question.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionOptionDTO {

    private Long id;

    @NotBlank(message = "选项标识不能为空")
    private String optionLabel;

    @NotBlank(message = "选项内容不能为空")
    private String optionContent;

    @NotNull(message = "选项正确标识不能为空")
    @Min(value = 0, message = "选项正确标识非法")
    @Max(value = 1, message = "选项正确标识非法")
    private Integer isCorrect;

    private Integer sortNo;
}

