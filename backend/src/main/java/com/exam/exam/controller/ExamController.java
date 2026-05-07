package com.exam.exam.controller;

import com.exam.common.security.SecurityUtils;
import com.exam.common.web.ApiResponse;
import com.exam.exam.entity.Exam;
import com.exam.exam.entity.ExamPaper;
import com.exam.exam.mapper.ExamMapper;
import com.exam.exam.mapper.ExamPaperMapper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher/exams")
public class ExamController {

    private final ExamMapper examMapper;
    private final ExamPaperMapper examPaperMapper;

    public ExamController(ExamMapper examMapper, ExamPaperMapper examPaperMapper) {
        this.examMapper = examMapper;
        this.examPaperMapper = examPaperMapper;
    }

    @PostMapping
    public ApiResponse<Long> create(@RequestBody Map<String, Object> body) {
        Exam exam = new Exam();
        exam.setExamName((String) body.get("examName"));
        exam.setPaperId(Long.valueOf(body.get("paperId").toString()));
        exam.setExamMode((Integer) body.getOrDefault("examMode", 1));
        exam.setPublishStatus(0); // draft
        exam.setStartTime(LocalDateTime.parse((String) body.get("startTime")));
        exam.setEndTime(LocalDateTime.parse((String) body.get("endTime")));
        exam.setDurationMinutes((Integer) body.get("durationMinutes"));
        exam.setTotalScore(new BigDecimal(body.getOrDefault("totalScore", "0").toString()));
        exam.setPassScore(new BigDecimal(body.getOrDefault("passScore", "60").toString()));
        exam.setAntiCheatSwitch((Integer) body.getOrDefault("antiCheatSwitch", 0));
        exam.setCutScreenLimit((Integer) body.getOrDefault("cutScreenLimit", 3));
        exam.setAutoSubmitSwitch((Integer) body.getOrDefault("autoSubmitSwitch", 1));
        exam.setAllowRepeatEnter((Integer) body.getOrDefault("allowRepeatEnter", 1));
        exam.setRemark((String) body.get("remark"));
        exam.setCreatorId(SecurityUtils.getCurrentUserId());
        examMapper.insert(exam);
        return ApiResponse.success(exam.getId());
    }

    @GetMapping("/{id}")
    public ApiResponse<Exam> get(@PathVariable Long id) {
        return ApiResponse.success(examMapper.selectById(id));
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable Long id) {
        examMapper.updateStatus(id, 1);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/close")
    public ApiResponse<Void> close(@PathVariable Long id) {
        examMapper.updateStatus(id, 2);
        return ApiResponse.success();
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(@RequestParam(required = false) Integer publishStatus,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        Long creatorId = SecurityUtils.getCurrentUserId();
        int offset = (pageNum - 1) * pageSize;
        var records = examMapper.selectList(creatorId, publishStatus, keyword, offset, pageSize);
        long total = examMapper.countList(creatorId, publishStatus, keyword);
        return ApiResponse.success(Map.of("records", records, "total", total, "pageNum", pageNum, "pageSize", pageSize));
    }

    @GetMapping("/{id}/monitor")
    public ApiResponse<Map<String, Object>> monitor(@PathVariable Long id) {
        List<Map<String, Object>> records = examPaperMapper.selectMonitorByExamId(id);
        return ApiResponse.success(Map.of("records", records));
    }
}
