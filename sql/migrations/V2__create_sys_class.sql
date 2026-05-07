-- V2: 创建班级独立表
CREATE TABLE IF NOT EXISTS sys_class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    grade_name VARCHAR(100) DEFAULT NULL COMMENT '年级',
    dept_name VARCHAR(100) DEFAULT NULL COMMENT '院系',
    teacher_id BIGINT DEFAULT NULL COMMENT '班主任/负责教师ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0禁用 1启用',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_class_name (class_name),
    KEY idx_sys_class_teacher_id (teacher_id),
    CONSTRAINT fk_sys_class_teacher_id FOREIGN KEY (teacher_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';
