-- ========== 重排班级ID（从1开始连续） ==========
-- mysql -u root -p online_exam < sql/fix_class_ids.sql

SET FOREIGN_KEY_CHECKS = 0;

-- 建映射表
DROP TEMPORARY TABLE IF EXISTS tmp_class_map;
CREATE TEMPORARY TABLE tmp_class_map AS
  SELECT id AS old_id, ROW_NUMBER() OVER (ORDER BY id) AS new_id
  FROM sys_class;

-- 更新引用表
UPDATE sys_user u JOIN tmp_class_map m ON u.class_id = m.old_id SET u.class_id = m.new_id;
UPDATE sys_class sc JOIN tmp_class_map m ON sc.teacher_id = m.old_id SET sc.teacher_id = m.new_id;

-- 更新主表
UPDATE sys_class c JOIN tmp_class_map m ON c.id = m.old_id SET c.id = m.new_id;

ALTER TABLE sys_class AUTO_INCREMENT = 1;
DROP TEMPORARY TABLE tmp_class_map;

SET FOREIGN_KEY_CHECKS = 1;

SELECT '=== 班级ID重排完成 ===' AS result;
SELECT id, class_name FROM sys_class ORDER BY id;
