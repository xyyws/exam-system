package com.exam.system.service;

import com.exam.common.web.PageResponse;
import com.exam.system.dto.request.ClassQueryRequest;
import com.exam.system.dto.request.ClassCreateRequest;
import com.exam.system.dto.response.ClassResponse;

public interface ClassService {
    PageResponse<ClassResponse> listClasses(ClassQueryRequest request);
    ClassResponse getClass(Long id);
    Long createClass(ClassCreateRequest request);
    void updateClass(Long id, ClassCreateRequest request);
    void updateStatus(Long id, Integer status);
    void deleteClass(Long id);
}
