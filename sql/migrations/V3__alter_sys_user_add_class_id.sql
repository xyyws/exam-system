-- V3: sys_user 增加 class_id 外键
ALTER TABLE sys_user
    ADD COLUMN class_id BIGINT DEFAULT NULL COMMENT '班级ID' AFTER dept_name,
    ADD KEY idx_sys_user_class_id (class_id),
    ADD CONSTRAINT fk_sys_user_class_id FOREIGN KEY (class_id) REFERENCES sys_class(id);
