package com.exam.runtime.controller;

import com.exam.common.security.SecurityUtils;
import com.exam.common.web.ApiResponse;
import com.exam.exam.entity.ExamPaper;
import com.exam.exam.mapper.ExamPaperMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/exams")
public class StudentRecordController {

    private final ExamPaperMapper examPaperMapper;

    public StudentRecordController(ExamPaperMapper examPaperMapper) {
        this.examPaperMapper = examPaperMapper;
    }

    @GetMapping("/records")
    public ApiResponse<Map<String, Object>> records() {
        Long studentId = SecurityUtils.getCurrentUserId();
        List<ExamPaper> papers = examPaperMapper.selectByStudentId(studentId);
        return ApiResponse.success(Map.of("records", papers));
    }
}
