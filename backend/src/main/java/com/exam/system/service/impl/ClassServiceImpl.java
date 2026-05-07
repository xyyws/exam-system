package com.exam.system.service.impl;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.security.SecurityUtils;
import com.exam.common.web.PageResponse;
import com.exam.system.dto.request.ClassCreateRequest;
import com.exam.system.dto.request.ClassQueryRequest;
import com.exam.system.dto.response.ClassResponse;
import com.exam.system.entity.SysClass;
import com.exam.system.entity.SysUser;
import com.exam.system.mapper.SysClassMapper;
import com.exam.system.mapper.SysUserMapper;
import com.exam.system.service.ClassService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassServiceImpl implements ClassService {

    private final SysClassMapper classMapper;
    private final SysUserMapper userMapper;

    public ClassServiceImpl(SysClassMapper classMapper, SysUserMapper userMapper) {
        this.classMapper = classMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageResponse<ClassResponse> listClasses(ClassQueryRequest req) {
        int offset = (req.getPageNum() - 1) * req.getPageSize();
        List<SysClass> list = classMapper.selectList(req.getKeyword(), req.getStatus(), offset, req.getPageSize());
        long total = classMapper.countList(req.getKeyword(), req.getStatus());
        List<ClassResponse> records = list.stream().map(this::toResponse).collect(Collectors.toList());
        return new PageResponse<>(total, req.getPageNum(), req.getPageSize(), records);
    }

    @Override
    public ClassResponse getClass(Long id) {
        SysClass cls = classMapper.selectById(id);
        if (cls == null) throw new BusinessException(ApiCodeEnum.CLASS_NOT_FOUND);
        return toResponse(cls);
    }

    @Override
    public Long createClass(ClassCreateRequest req) {
        SysClass cls = new SysClass();
        cls.setClassName(req.getClassName());
        cls.setGradeName(req.getGradeName());
        cls.setDeptName(req.getDeptName());
        cls.setTeacherId(req.getTeacherId());
        cls.setStatus(req.getStatus());
        cls.setRemark(req.getRemark());
        cls.setCreateBy(SecurityUtils.getCurrentUserId());
        classMapper.insert(cls);
        return cls.getId();
    }

    @Override
    public void updateClass(Long id, ClassCreateRequest req) {
        SysClass cls = classMapper.selectById(id);
        if (cls == null) throw new BusinessException(ApiCodeEnum.CLASS_NOT_FOUND);
        cls.setClassName(req.getClassName());
        cls.setGradeName(req.getGradeName());
        cls.setDeptName(req.getDeptName());
        cls.setTeacherId(req.getTeacherId());
        cls.setStatus(req.getStatus());
        cls.setRemark(req.getRemark());
        cls.setUpdateBy(SecurityUtils.getCurrentUserId());
        classMapper.update(cls);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysClass cls = classMapper.selectById(id);
        if (cls == null) throw new BusinessException(ApiCodeEnum.CLASS_NOT_FOUND);
        cls.setStatus(status);
        classMapper.update(cls);
    }

    private ClassResponse toResponse(SysClass c) {
        ClassResponse r = new ClassResponse();
        r.setId(c.getId());
        r.setClassName(c.getClassName());
        r.setGradeName(c.getGradeName());
        r.setDeptName(c.getDeptName());
        r.setTeacherId(c.getTeacherId());
        r.setStatus(c.getStatus());
        r.setRemark(c.getRemark());
        r.setCreateTime(c.getCreateTime());
        if (c.getTeacherId() != null) {
            SysUser teacher = userMapper.selectById(c.getTeacherId());
            if (teacher != null) r.setTeacherName(teacher.getRealName());
        }
        return r;
    }
}
