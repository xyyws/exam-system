package com.exam.system.controller;

import com.exam.common.web.ApiResponse;
import com.exam.common.web.PageResponse;
import com.exam.system.entity.OperationLog;
import com.exam.system.mapper.OperationLogMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminLogController {

    private final OperationLogMapper logMapper;

    public AdminLogController(OperationLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @GetMapping
    public ApiResponse<PageResponse<OperationLog>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<OperationLog> records = logMapper.selectList(keyword, status, offset, pageSize);
        long total = logMapper.countList(keyword, status);
        return ApiResponse.success(new PageResponse<>(total, pageNum, pageSize, records));
    }
}
