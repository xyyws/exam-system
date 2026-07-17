-- ========== 清空所有数据并重新导入种子数据 ==========
-- 执行: mysql -u root -p online_exam < sql/reset_all.sql

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE wrong_question_book;
TRUNCATE TABLE exam_answer;
TRUNCATE TABLE exam_paper;
TRUNCATE TABLE exam;
TRUNCATE TABLE paper_rule_detail;
TRUNCATE TABLE paper_rule;
TRUNCATE TABLE paper_question;
TRUNCATE TABLE paper;
TRUNCATE TABLE question_option;
TRUNCATE TABLE question;
TRUNCATE TABLE question_category;
TRUNCATE TABLE operation_log;
TRUNCATE TABLE sys_user_role;
TRUNCATE TABLE sys_user;
TRUNCATE TABLE sys_class;
TRUNCATE TABLE sys_role;

SET FOREIGN_KEY_CHECKS = 1;

-- 重新插入角色
INSERT INTO sys_role (role_code, role_name, status) VALUES
('ADMIN', '管理员', 1), ('TEACHER', '教师', 1), ('STUDENT', '学生', 1);
