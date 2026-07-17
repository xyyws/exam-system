package com.exam.system.controller;

import com.exam.common.web.ApiResponse;
import com.exam.common.web.PageResponse;
import com.exam.system.dto.request.ClassCreateRequest;
import com.exam.system.dto.request.ClassQueryRequest;
import com.exam.system.dto.response.ClassResponse;
import com.exam.system.service.ClassService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/classes")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) { this.classService = classService; }

    @GetMapping
    public ApiResponse<PageResponse<ClassResponse>> list(ClassQueryRequest query) {
        return ApiResponse.success(classService.listClasses(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassResponse> get(@PathVariable Long id) {
        return ApiResponse.success(classService.getClass(id));
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody ClassCreateRequest request) {
        return ApiResponse.success(classService.createClass(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ClassCreateRequest request) {
        classService.updateClass(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        classService.updateStatus(id, body.get("status"));
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        classService.deleteClass(id);
        return ApiResponse.success();
    }
}
