package com.exam.paper.controller;

import com.exam.common.security.SecurityUtils;
import com.exam.common.web.ApiResponse;
import com.exam.common.web.PageResponse;
import com.exam.paper.entity.Paper;
import com.exam.paper.mapper.PaperMapper;
import com.exam.paper.service.PaperAssemblyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher/papers")
public class PaperController {

    private final PaperAssemblyService paperAssemblyService;
    private final PaperMapper paperMapper;

    public PaperController(PaperAssemblyService paperAssemblyService, PaperMapper paperMapper) {
        this.paperAssemblyService = paperAssemblyService;
        this.paperMapper = paperMapper;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(@RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) Integer paperType,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        Long creatorId = SecurityUtils.getCurrentUserId();
        int offset = (pageNum - 1) * pageSize;
        List<Paper> records = paperMapper.selectList(keyword, paperType, 1, creatorId, offset, pageSize);
        long total = paperMapper.countList(keyword, paperType, 1, creatorId);
        return ApiResponse.success(Map.of("records", records, "total", total, "pageNum", pageNum, "pageSize", pageSize));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        paperMapper.softDelete(id);
        return ApiResponse.success();
    }

    @PostMapping("/manual")
    public ApiResponse<Long> createManual(@RequestBody Map<String, Object> body) {
        String paperName = (String) body.get("paperName");
        Integer duration = (Integer) body.getOrDefault("durationMinutes", 60);
        String remark = (String) body.get("remark");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) body.get("questionItems");

        List<PaperAssemblyService.ManualQuestionItem> items = itemsRaw.stream().map(m -> {
            PaperAssemblyService.ManualQuestionItem item = new PaperAssemblyService.ManualQuestionItem();
            item.setQuestionId(Long.valueOf(m.get("questionId").toString()));
            item.setScore(new java.math.BigDecimal(m.get("score").toString()));
            item.setQuestionOrder(Integer.valueOf(m.getOrDefault("questionOrder", 1).toString()));
            return item;
        }).toList();

        return ApiResponse.success(paperAssemblyService.createManualPaper(paperName, duration, remark, items));
    }

    @PostMapping("/auto-generate")
    public ApiResponse<Map<String, Object>> autoGenerate(@RequestBody Map<String, Object> body) {
        String paperName = (String) body.get("paperName");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rulesRaw = (List<Map<String, Object>>) body.get("rules");
        boolean preview = Boolean.TRUE.equals(body.get("preview"));

        List<PaperAssemblyService.RuleDetailInput> rules = rulesRaw.stream().map(m -> {
            PaperAssemblyService.RuleDetailInput r = new PaperAssemblyService.RuleDetailInput();
            r.setQuestionType((Integer) m.get("questionType"));
            r.setCategoryId(m.get("categoryId") != null ? Long.valueOf(m.get("categoryId").toString()) : null);
            r.setDifficulty((Integer) m.get("difficulty"));
            r.setQuestionCount((Integer) m.get("questionCount"));
            r.setScorePerQuestion(m.get("scorePerQuestion") != null ? new java.math.BigDecimal(m.get("scorePerQuestion").toString()) : null);
            return r;
        }).toList();

        if (preview) {
            return ApiResponse.success(paperAssemblyService.previewAutoPaper(rules, paperName));
        }
        Integer duration = (Integer) body.getOrDefault("durationMinutes", 60);
        String remark = (String) body.get("remark");
        Long id = paperAssemblyService.generateAutoPaper(paperName, duration, remark, rules);
        return ApiResponse.success(Map.of("id", id));
    }
}
