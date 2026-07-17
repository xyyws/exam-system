-- ========== 重排学生ID（admin+教师之后连续） ==========
-- mysql -u root -p online_exam < sql/fix_student_ids.sql
-- 注意：先执行 fix_teacher_ids.sql，再执行本脚本

SET FOREIGN_KEY_CHECKS = 0;

-- 计算起始ID = admin + 教师数量 + 1
SET @start_id = (SELECT COUNT(*) FROM sys_user WHERE user_type IN (1, 2) AND deleted = 0) + 1;

-- 建映射表
DROP TEMPORARY TABLE IF EXISTS tmp_student_map;
CREATE TEMPORARY TABLE tmp_student_map AS
  SELECT id AS old_id,
         ROW_NUMBER() OVER (ORDER BY id) + @start_id - 1 AS new_id
  FROM sys_user
  WHERE user_type = 3 AND deleted = 0;

-- 先更新所有引用表
ALTER TABLE sys_user ADD COLUMN tmp_new_id BIGINT DEFAULT NULL;
UPDATE sys_user u JOIN tmp_student_map m ON u.id = m.old_id SET u.tmp_new_id = m.new_id;

UPDATE sys_user_role SET user_id = (SELECT new_id FROM tmp_student_map WHERE old_id = user_id) WHERE user_id IN (SELECT old_id FROM tmp_student_map);
UPDATE exam_paper SET student_id = (SELECT new_id FROM tmp_student_map WHERE old_id = student_id) WHERE student_id IN (SELECT old_id FROM tmp_student_map);
UPDATE exam_answer SET student_id = (SELECT new_id FROM tmp_student_map WHERE old_id = student_id) WHERE student_id IN (SELECT old_id FROM tmp_student_map);
UPDATE wrong_question_book SET student_id = (SELECT new_id FROM tmp_student_map WHERE old_id = student_id) WHERE student_id IN (SELECT old_id FROM tmp_student_map);
UPDATE exam_score SET student_id = (SELECT new_id FROM tmp_student_map WHERE old_id = student_id) WHERE student_id IN (SELECT old_id FROM tmp_student_map);

-- 替换主表ID
UPDATE sys_user SET id = tmp_new_id WHERE tmp_new_id IS NOT NULL;
ALTER TABLE sys_user DROP COLUMN tmp_new_id;

ALTER TABLE sys_user AUTO_INCREMENT = 1;
DROP TEMPORARY TABLE tmp_student_map;

SET FOREIGN_KEY_CHECKS = 1;

SELECT '=== 学生ID重排完成 ===' AS result;
SELECT id, username, real_name, student_no FROM sys_user WHERE user_type = 3 ORDER BY id;
