package com.exam.question.controller;

import com.exam.common.web.ApiResponse;
import com.exam.question.entity.QuestionCategory;
import com.exam.question.service.QuestionCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/question-categories")
public class QuestionCategoryController {

    private final QuestionCategoryService service;

    public QuestionCategoryController(QuestionCategoryService service) { this.service = service; }

    @GetMapping
    public ApiResponse<List<QuestionCategory>> list(@RequestParam(required = false) Long parentId) {
        if (parentId != null) return ApiResponse.success(service.listByParent(parentId));
        return ApiResponse.success(service.listAll());
    }

    @PostMapping
    public ApiResponse<Long> create(@RequestBody QuestionCategory category) {
        return ApiResponse.success(service.create(category));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody QuestionCategory category) {
        service.update(id, category);
        return ApiResponse.success();
    }
}
