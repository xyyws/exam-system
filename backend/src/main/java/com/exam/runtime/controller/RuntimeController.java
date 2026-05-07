package com.exam.runtime.controller;

import com.exam.common.security.SecurityUtils;
import com.exam.common.web.ApiResponse;
import com.exam.runtime.service.RuntimeService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/exams")
public class RuntimeController {

    private final RuntimeService runtimeService;

    public RuntimeController(RuntimeService runtimeService) { this.runtimeService = runtimeService; }

    @GetMapping("/available")
    public ApiResponse<Map<String, Object>> available() {
        Long studentId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(runtimeService.getAvailableExams(studentId));
    }

    @GetMapping("/ongoing")
    public ApiResponse<Map<String, Object>> ongoing() {
        Long studentId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(runtimeService.getOngoingExam(studentId));
    }

    @PostMapping("/{examId}/enter")
    public ApiResponse<Map<String, Object>> enter(@PathVariable Long examId, @RequestBody Map<String, String> body) {
        Long studentId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(runtimeService.enterExam(examId, studentId, body.getOrDefault("deviceInfo", "")));
    }

    @PostMapping("/{examId}/answers/save")
    public ApiResponse<Void> saveAnswer(@PathVariable Long examId, @RequestBody Map<String, Object> body) {
        Long examPaperId = Long.valueOf(body.get("examPaperId").toString());
        runtimeService.saveAnswer(examId, examPaperId, Long.valueOf(body.get("questionId").toString()),
                (String) body.get("studentAnswer"));
        return ApiResponse.success();
    }

    @PostMapping("/{examId}/anti-cheat/cut-screen")
    public ApiResponse<Map<String, Object>> reportCutScreen(@PathVariable Long examId,
                                                             @RequestBody Map<String, Object> body) {
        Long examPaperId = Long.valueOf(body.get("examPaperId").toString());
        return ApiResponse.success(runtimeService.reportCutScreen(examId, examPaperId));
    }

    @PostMapping("/{examId}/submit")
    public ApiResponse<Map<String, Object>> submit(@PathVariable Long examId, @RequestBody Map<String, Object> body) {
        Long examPaperId = Long.valueOf(body.get("examPaperId").toString());
        boolean autoSubmit = Boolean.TRUE.equals(body.get("autoSubmit"));
        return ApiResponse.success(runtimeService.submitExam(examId, examPaperId, autoSubmit));
    }
}
