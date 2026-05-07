package com.exam.question.controller;

import com.exam.common.web.ApiResponse;
import com.exam.common.web.PageResponse;
import com.exam.question.dto.QuestionQueryRequest;
import com.exam.question.dto.QuestionResponse;
import com.exam.question.dto.QuestionSaveRequest;
import com.exam.question.service.QuestionImportService;
import com.exam.question.service.QuestionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Validated
@RestController
@RequestMapping("/api/teacher/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionImportService questionImportService;

    public QuestionController(QuestionService questionService, QuestionImportService questionImportService) {
        this.questionService = questionService;
        this.questionImportService = questionImportService;
    }

    @PostMapping
    public ApiResponse<Long> createQuestion(@Valid @RequestBody QuestionSaveRequest request) {
        return ApiResponse.success(questionService.createQuestion(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateQuestion(@PathVariable @Min(1) Long id,
                                            @Valid @RequestBody QuestionSaveRequest request) {
        questionService.updateQuestion(id, request);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    public ApiResponse<QuestionResponse> getQuestionDetail(@PathVariable @Min(1) Long id) {
        return ApiResponse.success(questionService.getQuestionDetail(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<QuestionResponse>> pageQuestions(@Valid QuestionQueryRequest request) {
        return ApiResponse.success(questionService.pageQuestions(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQuestion(@PathVariable @Min(1) Long id) {
        questionService.deleteQuestion(id);
        return ApiResponse.success();
    }

    // ─── 批量导入 ───

    @PostMapping("/import")
    public ApiResponse<QuestionImportService.ImportResult> importQuestions(
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new com.exam.common.exception.BusinessException("B301", "请选择要导入的文件");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new com.exam.common.exception.BusinessException("B302", "仅支持 .xlsx / .xls 格式的 Excel 文件");
        }
        QuestionImportService.ImportResult result = questionImportService.importFromExcel(file);
        return ApiResponse.success(result);
    }

    @GetMapping("/import/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        ByteArrayOutputStream out = questionImportService.generateTemplate();
        String encodedName = URLEncoder.encode("题目导入模板.xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName);
        response.getOutputStream().write(out.toByteArray());
        response.getOutputStream().flush();
    }
}

