package com.exam.question.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface QuestionImportService {

    /**
     * 从 Excel 文件批量导入题目
     * @param file 上传的 Excel 文件
     * @return 导入结果
     */
    ImportResult importFromExcel(MultipartFile file);

    /**
     * 生成导入模板 Excel
     * @return 模板文件字节流
     */
    ByteArrayOutputStream generateTemplate();

    /**
     * 导入结果
     */
    class ImportResult {
        private int successCount;
        private int failCount;
        private List<String> errors;

        public ImportResult(int successCount, int failCount, List<String> errors) {
            this.successCount = successCount;
            this.failCount = failCount;
            this.errors = errors;
        }

        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailCount() { return failCount; }
        public void setFailCount(int failCount) { this.failCount = failCount; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }
}
