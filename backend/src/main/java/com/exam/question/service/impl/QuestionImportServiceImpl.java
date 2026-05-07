package com.exam.question.service.impl;

import com.exam.common.security.SecurityUtils;
import com.exam.question.dto.QuestionOptionDTO;
import com.exam.question.dto.QuestionSaveRequest;
import com.exam.question.entity.Question;
import com.exam.question.entity.QuestionOption;
import com.exam.question.mapper.QuestionMapper;
import com.exam.question.mapper.QuestionOptionMapper;
import com.exam.question.service.QuestionImportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class QuestionImportServiceImpl implements QuestionImportService {

    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;

    private static final Map<String, Integer> TYPE_MAP = Map.of(
            "单选题", 1, "多选题", 2, "判断题", 3, "填空题", 4, "简答题", 5
    );

    private static final Map<String, Integer> DIFF_MAP = Map.of(
            "简单", 1, "较易", 2, "中等", 3, "较难", 4, "困难", 5
    );

    public QuestionImportServiceImpl(QuestionMapper questionMapper, QuestionOptionMapper questionOptionMapper) {
        this.questionMapper = questionMapper;
        this.questionOptionMapper = questionOptionMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResult importFromExcel(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getLastRowNum() < 1) {
                errors.add("文件为空或没有数据行");
                return new ImportResult(0, 0, errors);
            }

            // 跳过表头，从第2行开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                try {
                    QuestionSaveRequest request = parseRow(row, i + 1);
                    createQuestionFromRequest(request);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errors.add("第" + (i + 1) + "行: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            errors.add("文件读取失败: " + e.getMessage());
        }

        return new ImportResult(successCount, failCount, errors);
    }

    @Override
    public ByteArrayOutputStream generateTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("题目导入模板");

            // 表头
            Row header = sheet.createRow(0);
            String[] headers = {"题型", "题干", "选项A", "选项B", "选项C", "选项D", "正确答案", "分值", "难度", "解析"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // 示例数据
            Object[][] examples = {
                    {"单选题", "Java中哪个关键字用于定义类？", "class", "public", "void", "static", "A", 2, "中等", "class是定义类的关键字"},
                    {"多选题", "以下哪些是基本数据类型？", "int", "String", "boolean", "List", "A,C", 3, "简单", "String和List是引用类型"},
                    {"判断题", "Java支持多继承", "", "", "", "", "F", 2, "简单", "Java只支持单继承"},
                    {"填空题", "Java的父类是____", "", "", "", "", "Object", 2, "中等", "所有类都继承Object"},
                    {"简答题", "请解释多态的概念", "", "", "", "", "多态是指同一操作作用于不同对象时产生不同行为", 5, "困难", "需要解释核心概念"}
            };

            for (int i = 0; i < examples.length; i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < examples[i].length; j++) {
                    Cell cell = row.createCell(j);
                    Object val = examples[i][j];
                    if (val instanceof Integer) {
                        cell.setCellValue((Integer) val);
                    } else {
                        cell.setCellValue(val != null ? val.toString() : "");
                    }
                }
            }

            // 列宽
            int[] widths = {12, 40, 15, 15, 15, 15, 15, 8, 8, 30};
            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i] * 256);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out;
        } catch (IOException e) {
            throw new RuntimeException("生成模板失败", e);
        }
    }

    private QuestionSaveRequest parseRow(Row row, int rowNum) {
        // 题型
        String typeStr = getCellString(row, 0);
        if (typeStr == null || typeStr.isBlank()) {
            throw new RuntimeException("题型不能为空");
        }
        Integer questionType = TYPE_MAP.get(typeStr.trim());
        if (questionType == null) {
            throw new RuntimeException("题型不合法，支持: 单选题/多选题/判断题/填空题/简答题");
        }

        // 题干
        String title = getCellString(row, 1);
        if (title == null || title.isBlank()) {
            throw new RuntimeException("题干不能为空");
        }

        // 正确答案
        String correctAnswer = getCellString(row, 6);
        if (correctAnswer == null || correctAnswer.isBlank()) {
            throw new RuntimeException("正确答案不能为空");
        }

        // 分值
        BigDecimal score;
        try {
            String scoreStr = getCellString(row, 7);
            score = new BigDecimal(scoreStr != null && !scoreStr.isBlank() ? scoreStr : "0");
            if (score.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("分值必须大于0");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("分值格式不正确");
        }

        // 难度
        String diffStr = getCellString(row, 8);
        Integer difficulty = DIFF_MAP.getOrDefault(diffStr != null ? diffStr.trim() : "", 3);

        // 解析
        String analysis = getCellString(row, 9);

        QuestionSaveRequest request = new QuestionSaveRequest();
        request.setQuestionType(questionType);
        request.setTitle(title.trim());
        request.setDifficulty(difficulty);
        request.setScore(score);
        request.setCorrectAnswer(correctAnswer.trim());
        request.setAnswerAnalysis(analysis != null ? analysis.trim() : "");
        request.setStatus(1);

        // 处理选项
        if (questionType <= 3) {
            List<QuestionOptionDTO> options = new ArrayList<>();
            if (questionType == 3) {
                // 判断题：自动创建 对/错 选项
                QuestionOptionDTO optT = new QuestionOptionDTO();
                optT.setOptionLabel("T");
                optT.setOptionContent("正确");
                optT.setIsCorrect("T".equalsIgnoreCase(correctAnswer.trim()) ? 1 : 0);
                options.add(optT);

                QuestionOptionDTO optF = new QuestionOptionDTO();
                optF.setOptionLabel("F");
                optF.setOptionContent("错误");
                optF.setIsCorrect("F".equalsIgnoreCase(correctAnswer.trim()) ? 1 : 0);
                options.add(optF);
            } else {
                // 单选/多选：读取选项A-D
                String[] correctAnswers = correctAnswer.trim().toUpperCase().split("[,，]");
                Set<String> correctSet = new HashSet<>(Arrays.asList(correctAnswers));

                for (int col = 2; col <= 5; col++) {
                    String label = String.valueOf((char) ('A' + col - 2));
                    String content = getCellString(row, col);
                    if (content != null && !content.isBlank()) {
                        QuestionOptionDTO opt = new QuestionOptionDTO();
                        opt.setOptionLabel(label);
                        opt.setOptionContent(content.trim());
                        opt.setIsCorrect(correctSet.contains(label) ? 1 : 0);
                        options.add(opt);
                    }
                }

                if (options.isEmpty()) {
                    throw new RuntimeException("选择题至少需要一个选项");
                }
            }
            request.setOptions(options);
        }

        return request;
    }

    private void createQuestionFromRequest(QuestionSaveRequest request) {
        Question question = new Question();
        question.setQuestionType(request.getQuestionType());
        question.setTitle(request.getTitle());
        question.setDifficulty(request.getDifficulty());
        question.setScore(request.getScore());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setAnswerAnalysis(request.getAnswerAnalysis());
        question.setKnowledgePoints(request.getKnowledgePoints());
        question.setTags(request.getTags());
        question.setSource(request.getSource());
        question.setStatus(request.getStatus());
        question.setCreatorId(SecurityUtils.getCurrentUserId());
        question.setDeleted(0);
        questionMapper.insert(question);

        // 保存选项
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            List<QuestionOption> options = new ArrayList<>();
            for (QuestionOptionDTO dto : request.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setQuestionId(question.getId());
                option.setOptionLabel(dto.getOptionLabel());
                option.setOptionContent(dto.getOptionContent());
                option.setIsCorrect(dto.getIsCorrect());
                option.setSortNo(0);
                options.add(option);
            }
            questionOptionMapper.batchInsert(options);
        }
    }

    private String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            if (val == Math.floor(val) && !Double.isInfinite(val)) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);
        }
        return cell.getStringCellValue();
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < 7; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String val = getCellString(row, i);
                if (val != null && !val.isBlank()) return false;
            }
        }
        return true;
    }
}
