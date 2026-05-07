package com.exam.question.dto;

import lombok.Data;

@Data
public class QuestionQueryRequest {

    private String keyword;
    private Integer questionType;
    private Integer difficulty;
    private String knowledgePoints;
    private Integer status;
    private Long categoryId;
    private long pageNum = 1;
    private long pageSize = 10;

    public long getOffset() {
        long safePageNum = Math.max(pageNum, 1);
        long safePageSize = Math.max(pageSize, 1);
        return (safePageNum - 1) * safePageSize;
    }
}

