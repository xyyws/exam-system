-- ========== 重排教师ID（从2开始连续，1留给admin） ==========
-- mysql -u root -p online_exam < sql/fix_teacher_ids.sql

SET FOREIGN_KEY_CHECKS = 0;

-- 建映射表（admin保持id=1不动，教师从2开始）
DROP TEMPORARY TABLE IF EXISTS tmp_teacher_map;
CREATE TEMPORARY TABLE tmp_teacher_map AS
  SELECT id AS old_id,
         ROW_NUMBER() OVER (ORDER BY id) + 1 AS new_id
  FROM sys_user
  WHERE user_type = 2 AND deleted = 0;

-- 先更新所有引用表（用临时列避免冲突）
ALTER TABLE sys_user ADD COLUMN tmp_new_id BIGINT DEFAULT NULL;
UPDATE sys_user u JOIN tmp_teacher_map m ON u.id = m.old_id SET u.tmp_new_id = m.new_id;

-- 更新外键引用
UPDATE sys_class SET teacher_id = (SELECT new_id FROM tmp_teacher_map WHERE old_id = teacher_id) WHERE teacher_id IN (SELECT old_id FROM tmp_teacher_map);
UPDATE sys_user_role SET user_id = (SELECT new_id FROM tmp_teacher_map WHERE old_id = user_id) WHERE user_id IN (SELECT old_id FROM tmp_teacher_map);
UPDATE question SET creator_id = (SELECT new_id FROM tmp_teacher_map WHERE old_id = creator_id) WHERE creator_id IN (SELECT old_id FROM tmp_teacher_map);
UPDATE paper SET creator_id = (SELECT new_id FROM tmp_teacher_map WHERE old_id = creator_id) WHERE creator_id IN (SELECT old_id FROM tmp_teacher_map);
UPDATE exam SET creator_id = (SELECT new_id FROM tmp_teacher_map WHERE old_id = creator_id) WHERE creator_id IN (SELECT old_id FROM tmp_teacher_map);
UPDATE exam_answer SET mark_teacher_id = (SELECT new_id FROM tmp_teacher_map WHERE old_id = mark_teacher_id) WHERE mark_teacher_id IN (SELECT old_id FROM tmp_teacher_map);
UPDATE operation_log SET user_id = (SELECT new_id FROM tmp_teacher_map WHERE old_id = user_id) WHERE user_id IN (SELECT old_id FROM tmp_teacher_map);

-- 替换主表ID
UPDATE sys_user SET id = tmp_new_id WHERE tmp_new_id IS NOT NULL;
ALTER TABLE sys_user DROP COLUMN tmp_new_id;

ALTER TABLE sys_user AUTO_INCREMENT = 1;
DROP TEMPORARY TABLE tmp_teacher_map;

SET FOREIGN_KEY_CHECKS = 1;

SELECT '=== 教师ID重排完成 ===' AS result;
SELECT id, username, real_name FROM sys_user WHERE user_type = 2 ORDER BY id;
