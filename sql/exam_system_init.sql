SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS wrong_question_book;
DROP TABLE IF EXISTS exam_score;
DROP TABLE IF EXISTS exam_answer;
DROP TABLE IF EXISTS exam_paper;
DROP TABLE IF EXISTS exam;
DROP TABLE IF EXISTS paper_rule_detail;
DROP TABLE IF EXISTS paper_rule;
DROP TABLE IF EXISTS paper_question;
DROP TABLE IF EXISTS paper;
DROP TABLE IF EXISTS question_option;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS question_category;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '登录账号',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别:0未知 1男 2女',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    user_type TINYINT NOT NULL COMMENT '用户类型:1管理员 2教师 3学生',
    student_no VARCHAR(50) DEFAULT NULL COMMENT '学号',
    teacher_no VARCHAR(50) DEFAULT NULL COMMENT '工号',
    dept_name VARCHAR(100) DEFAULT NULL COMMENT '院系',
    class_name VARCHAR(100) DEFAULT NULL COMMENT '班级',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0禁用 1启用',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否 1是',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_user_username (username),
    UNIQUE KEY uk_sys_user_student_no (student_no),
    UNIQUE KEY uk_sys_user_teacher_no (teacher_no),
    KEY idx_sys_user_type (user_type),
    KEY idx_sys_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0禁用 1启用',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_sys_user_role (user_id, role_id),
    KEY idx_sur_role_id (role_id),
    CONSTRAINT fk_sur_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_sur_role_id FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

CREATE TABLE question_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0禁用 1启用',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_qc_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目分类表';

CREATE TABLE question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    question_type TINYINT NOT NULL COMMENT '题型:1单选 2多选 3判断 4填空 5简答',
    title TEXT NOT NULL COMMENT '题干',
    difficulty TINYINT NOT NULL DEFAULT 3 COMMENT '难度:1简单 2较易 3中等 4较难 5困难',
    score DECIMAL(8,2) NOT NULL DEFAULT 0.00 COMMENT '默认分值',
    correct_answer TEXT DEFAULT NULL COMMENT '标准答案(JSON/文本)',
    answer_analysis TEXT DEFAULT NULL COMMENT '答案解析',
    knowledge_points VARCHAR(500) DEFAULT NULL COMMENT '知识点,逗号分隔',
    tags VARCHAR(500) DEFAULT NULL COMMENT '标签,逗号分隔',
    source VARCHAR(255) DEFAULT NULL COMMENT '题目来源',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0停用 1启用',
    creator_id BIGINT NOT NULL COMMENT '创建人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否 1是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_question_category_id (category_id),
    KEY idx_question_type (question_type),
    KEY idx_question_difficulty (difficulty),
    KEY idx_question_creator_id (creator_id),
    CONSTRAINT fk_question_category_id FOREIGN KEY (category_id) REFERENCES question_category(id),
    CONSTRAINT fk_question_creator_id FOREIGN KEY (creator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库表';

CREATE TABLE question_option (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选项ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    option_label VARCHAR(10) NOT NULL COMMENT '选项标识:A/B/C/D',
    option_content TEXT NOT NULL COMMENT '选项内容',
    is_correct TINYINT NOT NULL DEFAULT 0 COMMENT '是否正确:0否 1是',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_qo_question_id (question_id),
    CONSTRAINT fk_qo_question_id FOREIGN KEY (question_id) REFERENCES question(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';

CREATE TABLE paper (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '试卷ID',
    paper_name VARCHAR(200) NOT NULL COMMENT '试卷名称',
    paper_type TINYINT NOT NULL DEFAULT 1 COMMENT '试卷类型:1手动组卷 2自动组卷',
    total_score DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总分',
    question_count INT NOT NULL DEFAULT 0 COMMENT '题目数量',
    duration_minutes INT NOT NULL DEFAULT 60 COMMENT '建议时长(分钟)',
    difficulty_level TINYINT DEFAULT NULL COMMENT '整体难度',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0停用 1启用',
    rule_json JSON DEFAULT NULL COMMENT '自动组卷规则JSON',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    creator_id BIGINT NOT NULL COMMENT '创建人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否 1是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_paper_type (paper_type),
    KEY idx_paper_creator_id (creator_id),
    CONSTRAINT fk_paper_creator_id FOREIGN KEY (creator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

CREATE TABLE paper_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    question_type TINYINT NOT NULL COMMENT '题型',
    question_order INT NOT NULL DEFAULT 1 COMMENT '题目序号',
    score DECIMAL(8,2) NOT NULL DEFAULT 0.00 COMMENT '本卷分值',
    question_snapshot JSON NOT NULL COMMENT '题目快照JSON',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_paper_question (paper_id, question_id),
    KEY idx_pq_question_id (question_id),
    KEY idx_pq_paper_order (paper_id, question_order),
    CONSTRAINT fk_pq_paper_id FOREIGN KEY (paper_id) REFERENCES paper(id),
    CONSTRAINT fk_pq_question_id FOREIGN KEY (question_id) REFERENCES question(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷试题关联表';

CREATE TABLE paper_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    total_score DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '规则总分',
    total_count INT NOT NULL DEFAULT 0 COMMENT '规则总题数',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_paper_rule_paper_id (paper_id),
    CONSTRAINT fk_pr_paper_id FOREIGN KEY (paper_id) REFERENCES paper(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自动组卷规则主表';

CREATE TABLE paper_rule_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则明细ID',
    rule_id BIGINT NOT NULL COMMENT '规则ID',
    question_type TINYINT NOT NULL COMMENT '题型',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    difficulty TINYINT DEFAULT NULL COMMENT '难度',
    question_count INT NOT NULL DEFAULT 0 COMMENT '抽题数量',
    score_per_question DECIMAL(8,2) NOT NULL DEFAULT 0.00 COMMENT '每题分值',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_prd_rule_id (rule_id),
    KEY idx_prd_category_id (category_id),
    CONSTRAINT fk_prd_rule_id FOREIGN KEY (rule_id) REFERENCES paper_rule(id),
    CONSTRAINT fk_prd_category_id FOREIGN KEY (category_id) REFERENCES question_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自动组卷规则明细表';

CREATE TABLE exam (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考试ID',
    exam_name VARCHAR(200) NOT NULL COMMENT '考试名称',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    exam_mode TINYINT NOT NULL DEFAULT 1 COMMENT '考试模式:1固定试卷 2随机题序 3随机选项序',
    publish_status TINYINT NOT NULL DEFAULT 0 COMMENT '发布状态:0草稿 1已发布 2已结束 3已归档',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    duration_minutes INT NOT NULL COMMENT '考试时长(分钟)',
    total_score DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总分',
    pass_score DECIMAL(10,2) NOT NULL DEFAULT 60.00 COMMENT '及格分',
    anti_cheat_switch TINYINT NOT NULL DEFAULT 0 COMMENT '防作弊开关:0关 1开',
    cut_screen_limit INT NOT NULL DEFAULT 0 COMMENT '切屏次数限制',
    auto_submit_switch TINYINT NOT NULL DEFAULT 1 COMMENT '到时自动交卷:0否 1是',
    allow_repeat_enter TINYINT NOT NULL DEFAULT 1 COMMENT '允许重进:0否 1是',
    creator_id BIGINT NOT NULL COMMENT '创建人',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_exam_paper_id (paper_id),
    KEY idx_exam_creator_id (creator_id),
    KEY idx_exam_time (start_time, end_time),
    CONSTRAINT fk_exam_paper_id FOREIGN KEY (paper_id) REFERENCES paper(id),
    CONSTRAINT fk_exam_creator_id FOREIGN KEY (creator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

CREATE TABLE exam_paper (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '答卷ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    attempt_no INT NOT NULL DEFAULT 1 COMMENT '第几次作答',
    answer_status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0未开始 1进行中 2已交卷 3已判分',
    start_time DATETIME DEFAULT NULL COMMENT '开始答题时间',
    submit_time DATETIME DEFAULT NULL COMMENT '交卷时间',
    auto_submit TINYINT NOT NULL DEFAULT 0 COMMENT '是否自动交卷:0否 1是',
    used_minutes INT NOT NULL DEFAULT 0 COMMENT '用时(分钟)',
    objective_score DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '客观题得分',
    subjective_score DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '主观题得分',
    total_score DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总得分',
    cut_screen_count INT NOT NULL DEFAULT 0 COMMENT '切屏次数',
    violation_count INT NOT NULL DEFAULT 0 COMMENT '违规次数',
    ip_address VARCHAR(64) DEFAULT NULL COMMENT 'IP地址',
    device_info VARCHAR(500) DEFAULT NULL COMMENT '设备信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_exam_student_attempt (exam_id, student_id, attempt_no),
    KEY idx_ep_exam_id (exam_id),
    KEY idx_ep_paper_id (paper_id),
    KEY idx_ep_student_id (student_id),
    CONSTRAINT fk_ep_exam_id FOREIGN KEY (exam_id) REFERENCES exam(id),
    CONSTRAINT fk_ep_paper_id FOREIGN KEY (paper_id) REFERENCES paper(id),
    CONSTRAINT fk_ep_student_id FOREIGN KEY (student_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答卷表';

CREATE TABLE exam_answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '答题记录ID',
    exam_paper_id BIGINT NOT NULL COMMENT '答卷ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    question_type TINYINT NOT NULL COMMENT '题型',
    question_order INT NOT NULL DEFAULT 1 COMMENT '题目序号',
    student_answer TEXT DEFAULT NULL COMMENT '学生答案(JSON/文本)',
    correct_answer TEXT DEFAULT NULL COMMENT '标准答案快照',
    is_correct TINYINT DEFAULT NULL COMMENT '是否正确:0否 1是 NULL待判分',
    score DECIMAL(8,2) NOT NULL DEFAULT 0.00 COMMENT '本题得分',
    max_score DECIMAL(8,2) NOT NULL DEFAULT 0.00 COMMENT '本题满分',
    answer_status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0未答 1已答 2已判分',
    mark_status TINYINT NOT NULL DEFAULT 0 COMMENT '阅卷状态:0未阅 1已阅',
    mark_teacher_id BIGINT DEFAULT NULL COMMENT '阅卷教师ID',
    mark_time DATETIME DEFAULT NULL COMMENT '阅卷时间',
    mark_comment VARCHAR(1000) DEFAULT NULL COMMENT '阅卷评语',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_exam_answer_unique (exam_paper_id, question_id),
    KEY idx_ea_exam_id (exam_id),
    KEY idx_ea_student_id (student_id),
    KEY idx_ea_question_id (question_id),
    KEY idx_ea_mark_teacher_id (mark_teacher_id),
    CONSTRAINT fk_ea_exam_paper_id FOREIGN KEY (exam_paper_id) REFERENCES exam_paper(id),
    CONSTRAINT fk_ea_exam_id FOREIGN KEY (exam_id) REFERENCES exam(id),
    CONSTRAINT fk_ea_paper_id FOREIGN KEY (paper_id) REFERENCES paper(id),
    CONSTRAINT fk_ea_question_id FOREIGN KEY (question_id) REFERENCES question(id),
    CONSTRAINT fk_ea_student_id FOREIGN KEY (student_id) REFERENCES sys_user(id),
    CONSTRAINT fk_ea_mark_teacher_id FOREIGN KEY (mark_teacher_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';

CREATE TABLE exam_score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '成绩ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    exam_paper_id BIGINT NOT NULL COMMENT '答卷ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    objective_score DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '客观题得分',
    subjective_score DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '主观题得分',
    total_score DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总分',
    pass_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否通过:0否 1是',
    ranking_no INT DEFAULT NULL COMMENT '排名',
    grade_level VARCHAR(20) DEFAULT NULL COMMENT '等级:A/B/C',
    publish_status TINYINT NOT NULL DEFAULT 0 COMMENT '是否发布成绩:0否 1是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_exam_score_exam_student (exam_id, student_id),
    UNIQUE KEY uk_exam_score_exam_paper (exam_paper_id),
    KEY idx_es_paper_id (paper_id),
    CONSTRAINT fk_es_exam_id FOREIGN KEY (exam_id) REFERENCES exam(id),
    CONSTRAINT fk_es_exam_paper_id FOREIGN KEY (exam_paper_id) REFERENCES exam_paper(id),
    CONSTRAINT fk_es_paper_id FOREIGN KEY (paper_id) REFERENCES paper(id),
    CONSTRAINT fk_es_student_id FOREIGN KEY (student_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

CREATE TABLE wrong_question_book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '错题记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    exam_id BIGINT DEFAULT NULL COMMENT '来源考试ID',
    exam_paper_id BIGINT DEFAULT NULL COMMENT '来源答卷ID',
    first_wrong_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次错题时间',
    last_wrong_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近错题时间',
    wrong_count INT NOT NULL DEFAULT 1 COMMENT '错误次数',
    latest_answer TEXT DEFAULT NULL COMMENT '最近错误答案',
    mastered_status TINYINT NOT NULL DEFAULT 0 COMMENT '掌握状态:0未掌握 1已掌握',
    mastered_time DATETIME DEFAULT NULL COMMENT '掌握时间',
    note_text VARCHAR(1000) DEFAULT NULL COMMENT '学生备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_wqb_student_question (student_id, question_id),
    KEY idx_wqb_exam_id (exam_id),
    KEY idx_wqb_exam_paper_id (exam_paper_id),
    CONSTRAINT fk_wqb_student_id FOREIGN KEY (student_id) REFERENCES sys_user(id),
    CONSTRAINT fk_wqb_question_id FOREIGN KEY (question_id) REFERENCES question(id),
    CONSTRAINT fk_wqb_exam_id FOREIGN KEY (exam_id) REFERENCES exam(id),
    CONSTRAINT fk_wqb_exam_paper_id FOREIGN KEY (exam_paper_id) REFERENCES exam_paper(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错题本表';

INSERT INTO sys_role (role_code, role_name, status) VALUES
('ADMIN', '管理员', 1),
('TEACHER', '教师', 1),
('STUDENT', '学生', 1);

SET FOREIGN_KEY_CHECKS = 1;

