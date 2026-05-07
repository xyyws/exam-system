package com.exam.system.controller;

import com.exam.common.web.ApiResponse;
import com.exam.exam.mapper.ExamMapper;
import com.exam.exam.mapper.ExamPaperMapper;
import com.exam.system.mapper.SysClassMapper;
import com.exam.system.mapper.SysUserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
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
        long examCount = examMapper.countAll();
        long participantCount = examPaperMapper.countParticipants();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalUsers", totalUsers);
        data.put("teacherCount", teacherCount);
        data.put("studentCount", studentCount);
        data.put("classCount", classCount);
        data.put("examCount", examCount);
        data.put("participantCount", participantCount);
        return ApiResponse.success(data);
    }
}
