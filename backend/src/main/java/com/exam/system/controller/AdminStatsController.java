package com.exam.system.controller;

import com.exam.common.web.ApiResponse;
import com.exam.common.web.PageResponse;
import com.exam.exam.mapper.ExamMapper;
import com.exam.exam.mapper.ExamPaperMapper;
import com.exam.system.mapper.SysClassMapper;
import com.exam.system.mapper.SysUserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    private final SysUserMapper userMapper;
    private final SysClassMapper classMapper;
    private final ExamMapper examMapper;
    private final ExamPaperMapper examPaperMapper;

    public AdminStatsController(SysUserMapper userMapper, SysClassMapper classMapper,
                                 ExamMapper examMapper, ExamPaperMapper examPaperMapper) {
        this.userMapper = userMapper;
        this.classMapper = classMapper;
        this.examMapper = examMapper;
        this.examPaperMapper = examPaperMapper;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> stats() {
        long totalUsers = userMapper.countList(null, null, null, null);
        long teacherCount = userMapper.countList(null, 2, null, null);
        long studentCount = userMapper.countList(null, 3, null, null);
        long classCount = classMapper.countList(null, null);
        long examCount = examMapper.countPublished();
        List<Map<String, Object>> teacherExamStats = examMapper.selectTeacherExamStats();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalUsers", totalUsers);
        data.put("teacherCount", teacherCount);
        data.put("studentCount", studentCount);
        data.put("classCount", classCount);
        data.put("examCount", examCount);
        data.put("teacherExamStats", teacherExamStats);
        return ApiResponse.success(data);
    }

    @GetMapping("/teacher-exams")
    public ApiResponse<List<Map<String, Object>>> teacherExams(@RequestParam Long creatorId) {
        List<Map<String, Object>> exams = examMapper.selectTeacherExams(creatorId);
        return ApiResponse.success(exams);
    }

    @GetMapping("/participants")
    public ApiResponse<PageResponse<Map<String, Object>>> participants(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String studentName) {
        int offset = (pageNum - 1) * pageSize;
        long total = examPaperMapper.countParticipantsByCondition(studentName);
        List<Map<String, Object>> records = examPaperMapper.selectParticipants(studentName, offset, pageSize);
        return ApiResponse.success(new PageResponse<>(total, pageNum, pageSize, records));
    }
}
