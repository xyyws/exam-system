package com.exam.exam.controller;

import com.exam.common.enums.ApiCodeEnum;
import com.exam.common.exception.BusinessException;
import com.exam.common.security.SecurityUtils;
import com.exam.common.web.ApiResponse;
import com.exam.exam.entity.Exam;
import com.exam.exam.entity.ExamPaper;
import com.exam.exam.mapper.ExamMapper;
import com.exam.exam.mapper.ExamPaperMapper;
import com.exam.paper.entity.Paper;
import com.exam.paper.mapper.PaperMapper;
import com.exam.system.entity.SysClass;
import com.exam.system.entity.SysUser;
import com.exam.system.mapper.SysClassMapper;
import com.exam.system.mapper.SysUserMapper;
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
    private final PaperMapper paperMapper;
    private final SysUserMapper userMapper;
    private final SysClassMapper classMapper;

    public ExamController(ExamMapper examMapper, ExamPaperMapper examPaperMapper,
                          PaperMapper paperMapper, SysUserMapper userMapper,
                          SysClassMapper classMapper) {
        this.examMapper = examMapper;
        this.examPaperMapper = examPaperMapper;
        this.paperMapper = paperMapper;
        this.userMapper = userMapper;
        this.classMapper = classMapper;
    }

    @GetMapping("/students")
    public ApiResponse<Map<String, Object>> getStudents(
            @RequestParam(required = false) Long classId,
            @RequestParam(defaultValue = "500") int pageSize) {
        List<SysUser> students = userMapper.selectList(null, 3, 1, classId, 0, pageSize);
        List<Map<String, Object>> list = students.stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("realName", u.getRealName());
            m.put("studentNo", u.getStudentNo());
            if (u.getClassId() != null) {
                m.put("classId", u.getClassId());
            }
            return m;
        }).collect(Collectors.toList());
        return ApiResponse.success(Map.of("records", list, "total", list.size()));
    }

    @GetMapping("/classes")
    public ApiResponse<Map<String, Object>> getClasses() {
        var classes = classMapper.selectList(null, 1, 0, 200);
        List<Map<String, Object>> list = classes.stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("className", c.getClassName());
            m.put("deptName", c.getDeptName());
            return m;
        }).collect(Collectors.toList());
        return ApiResponse.success(Map.of("records", list, "total", list.size()));
    }

    @PostMapping("/{examId}/assign-students")
    public ApiResponse<Map<String, Object>> assignStudents(@PathVariable Long examId,
                                                            @RequestBody Map<String, Object> body) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new com.exam.common.exception.BusinessException(
                    com.exam.common.enums.ApiCodeEnum.EXAM_NOT_FOUND.getCode(), "考试不存在");
        }

        @SuppressWarnings("unchecked")
        List<Number> studentIds = (List<Number>) body.get("studentIds");
        if (studentIds == null || studentIds.isEmpty()) {
            throw new com.exam.common.exception.BusinessException(
                    com.exam.common.enums.ApiCodeEnum.BAD_REQUEST.getCode(), "请选择至少一名学生");
        }

        int added = 0;
        int skipped = 0;
        for (Number sid : studentIds) {
            Long studentId = sid.longValue();
            ExamPaper existing = examPaperMapper.selectByExamAndStudent(examId, studentId);
            if (existing != null) {
                skipped++;
                continue;
            }
            ExamPaper ep = new ExamPaper();
            ep.setExamId(examId);
            ep.setPaperId(exam.getPaperId());
            ep.setStudentId(studentId);
            ep.setAnswerStatus(0);
            ep.setDeviceInfo("");
            examPaperMapper.insert(ep);
            added++;
        }

        return ApiResponse.success(Map.of("added", added, "skipped", skipped));
    }

    @GetMapping("/{examId}/assigned-students")
    public ApiResponse<Map<String, Object>> getAssignedStudents(@PathVariable Long examId) {
        List<ExamPaper> list = examPaperMapper.selectByExamId(examId);
        List<Long> studentIds = list.stream().map(ExamPaper::getStudentId).collect(Collectors.toList());
        return ApiResponse.success(Map.of("studentIds", studentIds, "total", studentIds.size()));
    }

    @PostMapping
    public ApiResponse<Long> create(@RequestBody Map<String, Object> body) {
        Long paperId = Long.valueOf(body.get("paperId").toString());
        Paper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new BusinessException(ApiCodeEnum.PAPER_NOT_FOUND.getCode(), "试卷不存在");
        }

        Exam exam = new Exam();
        exam.setExamName((String) body.get("examName"));
        exam.setPaperId(paperId);
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

    @GetMapping("/{id:\\d+}")
    public ApiResponse<Exam> get(@PathVariable Long id) {
        return ApiResponse.success(examMapper.selectById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) throw new BusinessException(ApiCodeEnum.EXAM_NOT_FOUND.getCode(), "考试不存在");

        exam.setExamName((String) body.getOrDefault("examName", exam.getExamName()));
        if (body.get("startTime") != null) exam.setStartTime(LocalDateTime.parse((String) body.get("startTime")));
        if (body.get("endTime") != null) exam.setEndTime(LocalDateTime.parse((String) body.get("endTime")));
        if (body.get("durationMinutes") != null) exam.setDurationMinutes((Integer) body.get("durationMinutes"));
        if (body.get("totalScore") != null) exam.setTotalScore(new BigDecimal(body.get("totalScore").toString()));
        if (body.get("passScore") != null) exam.setPassScore(new BigDecimal(body.get("passScore").toString()));
        if (body.get("examMode") != null) exam.setExamMode((Integer) body.get("examMode"));
        if (body.get("antiCheatSwitch") != null) exam.setAntiCheatSwitch((Integer) body.get("antiCheatSwitch"));
        if (body.get("cutScreenLimit") != null) exam.setCutScreenLimit((Integer) body.get("cutScreenLimit"));
        if (body.get("remark") != null) exam.setRemark((String) body.get("remark"));

        // 已结束/已归档的考试编辑后自动重置为草稿，以便重新发布
        if (exam.getPublishStatus() != null && exam.getPublishStatus() >= 2) {
            exam.setPublishStatus(0);
        }

        examMapper.update(exam);
        return ApiResponse.success();
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

    @PostMapping("/{examId}/extend-time")
    public ApiResponse<Void> extendTime(@PathVariable Long examId, @RequestBody Map<String, Object> body) {
        Long studentId = Long.valueOf(body.get("studentId").toString());
        Integer extraMinutes = (Integer) body.get("extraMinutes");
        ExamPaper ep = examPaperMapper.selectByExamAndStudent(examId, studentId);
        if (ep == null) return ApiResponse.success();
        int current = ep.getExtraMinutes() != null ? ep.getExtraMinutes() : 0;
        ep.setExtraMinutes(current + extraMinutes);
        examPaperMapper.update(ep);
        return ApiResponse.success();
    }
}
