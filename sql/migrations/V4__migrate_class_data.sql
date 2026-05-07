-- V4: 从 sys_user.class_name 迁移数据到 sys_class
INSERT INTO sys_class (class_name, grade_name, dept_name, status)
SELECT DISTINCT class_name, NULL, dept_name, 1
FROM sys_user
WHERE class_name IS NOT NULL AND class_name <> '';

UPDATE sys_user u
JOIN sys_class c ON u.class_name = c.class_name
SET u.class_id = c.id
WHERE u.class_name IS NOT NULL AND u.class_name <> '';
