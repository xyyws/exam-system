-- ========== 重排ID：学生、教师、班级 ==========
-- 注意：执行前请备份数据库！会修改外键关联

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 重排 sys_class
SET @rank = 0;
UPDATE sys_class SET id = (@rank := @rank + 1) ORDER BY id;
ALTER TABLE sys_class AUTO_INCREMENT = 1;
SELECT @max_class := MAX(id) FROM sys_class;
ALTER TABLE sys_class AUTO_INCREMENT = @max_class + 1;

-- 同步 sys_user.class_id
UPDATE sys_user u
  JOIN sys_class c ON u.class_id = c.id
  SET u.class_id = c.id;

-- 2. 重排 sys_user（管理员1、教师2-4、学生5起）
-- 先按角色排序：ADMIN < TEACHER < STUDENT，同角色内按原id排
SET @rank = 0;
CREATE TEMPORARY TABLE tmp_user_map AS
  SELECT id AS old_id, (@rank := @rank + 1) AS new_id
  FROM sys_user
  WHERE deleted = 0
  ORDER BY user_type, id;

-- 更新外键引用（先用临时字段避免冲突）
ALTER TABLE sys_user ADD COLUMN new_id BIGINT DEFAULT NULL;
UPDATE sys_user u JOIN tmp_user_map m ON u.id = m.old_id SET u.new_id = m.new_id;

-- 更新所有引用 sys_user.id 的表
UPDATE sys_user_role SET user_id = (SELECT new_id FROM tmp_user_map WHERE old_id = user_id);
UPDATE sys_class SET teacher_id = (SELECT new_id FROM tmp_user_map WHERE old_id = teacher_id) WHERE teacher_id IS NOT NULL;
UPDATE question SET creator_id = (SELECT new_id FROM tmp_user_map WHERE old_id = creator_id);
UPDATE paper SET creator_id = (SELECT new_id FROM tmp_user_map WHERE old_id = creator_id);
UPDATE exam SET creator_id = (SELECT new_id FROM tmp_user_map WHERE old_id = creator_id);
UPDATE exam_paper SET student_id = (SELECT new_id FROM tmp_user_map WHERE old_id = student_id);
UPDATE exam_answer SET student_id = (SELECT new_id FROM tmp_user_map WHERE old_id = student_id);
UPDATE exam_answer SET mark_teacher_id = (SELECT new_id FROM tmp_user_map WHERE old_id = mark_teacher_id) WHERE mark_teacher_id IS NOT NULL;
UPDATE wrong_question_book SET student_id = (SELECT new_id FROM tmp_user_map WHERE old_id = student_id);
UPDATE operation_log SET user_id = (SELECT new_id FROM tmp_user_map WHERE old_id = user_id) WHERE user_id IS NOT NULL;

-- 替换 sys_user.id
UPDATE sys_user SET id = new_id WHERE new_id IS NOT NULL;
ALTER TABLE sys_user DROP COLUMN new_id;

-- 重置自增
SELECT @max_user := MAX(id) FROM sys_user;
ALTER TABLE sys_user AUTO_INCREMENT = @max_user + 1;

DROP TEMPORARY TABLE tmp_user_map;

SET FOREIGN_KEY_CHECKS = 1;

-- 验证
SELECT '=== 班级 ===' AS info;
SELECT id, class_name FROM sys_class ORDER BY id;
SELECT '=== 教师 ===' AS info;
SELECT id, username, real_name FROM sys_user WHERE user_type = 2 ORDER BY id;
SELECT '=== 学生 ===' AS info;
SELECT id, username, real_name, student_no FROM sys_user WHERE user_type = 3 ORDER BY id;
