package com.exam.runtime.controller;

import com.exam.common.security.SecurityUtils;
import com.exam.common.web.ApiResponse;
import com.exam.exam.entity.ExamPaper;
import com.exam.exam.mapper.ExamPaperMapper;
import com.exam.runtime.mapper.ExamAnswerMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/exams")
public class StudentRecordController {

    private final ExamPaperMapper examPaperMapper;
    private final ExamAnswerMapper examAnswerMapper;

    public StudentRecordController(ExamPaperMapper examPaperMapper, ExamAnswerMapper examAnswerMapper) {
        this.examPaperMapper = examPaperMapper;
        this.examAnswerMapper = examAnswerMapper;
    }

    @GetMapping("/records")
    public ApiResponse<Map<String, Object>> records() {
        Long studentId = SecurityUtils.getCurrentUserId();
        List<ExamPaper> papers = examPaperMapper.selectByStudentId(studentId);
        return ApiResponse.success(Map.of("records", papers));
    }

    @GetMapping("/records/{examPaperId}")
    public ApiResponse<Map<String, Object>> recordDetail(@PathVariable Long examPaperId) {
        Long studentId = SecurityUtils.getCurrentUserId();
        ExamPaper paper = examPaperMapper.selectById(examPaperId);
        if (paper == null || !paper.getStudentId().equals(studentId)) {
            throw new com.exam.common.exception.BusinessException("B001", "答卷不存在");
        }
        var answers = examAnswerMapper.selectByExamPaperId(examPaperId);
        return ApiResponse.success(Map.of("paper", paper, "answers", answers));
    }
}
