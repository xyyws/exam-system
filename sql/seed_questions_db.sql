-- ========== 数据库 题库扩展脚本 ==========
-- 分类ID: 2 (数据库)
-- 题型: 1单选 2多选 3判断 4填空 5简答
-- 难度: 1简单 2较易 3中等 4较难 5困难
-- creator_id: 4 (wang教师)

-- ==================== 一、单选题 (10题) ====================

-- S1: SQL聚合函数
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 1, '以下哪个SQL聚合函数会忽略NULL值？', 1, 2, 'A', 'COUNT(*)不忽略NULL，但COUNT(列名)会忽略NULL。SUM/AVG/MAX/MIN都忽略NULL', '聚合函数,NULL', 'SQL基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'COUNT(列名)', 1, 1), (LAST_INSERT_ID(), 'B', 'COUNT(*)', 0, 2), (LAST_INSERT_ID(), 'C', '以上都不会', 0, 3), (LAST_INSERT_ID(), 'D', '以上都会', 0, 4);

-- S2: 事务隔离级别
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 1, '以下哪个事务隔离级别可以防止脏读？', 3, 2, 'B', 'READ COMMITTED及更高隔离级别可以防止脏读', '事务隔离级别', '事务', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'READ UNCOMMITTED', 0, 1), (LAST_INSERT_ID(), 'B', 'READ COMMITTED', 1, 2), (LAST_INSERT_ID(), 'C', '以上都不能', 0, 3), (LAST_INSERT_ID(), 'D', '只有SERIALIZABLE可以', 0, 4);

-- S3: 范式
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 1, '关系R(A,B,C)中，A->B, B->C，则R最高满足第几范式？', 4, 2, 'B', '存在传递依赖A->B->C，满足2NF但不满足3NF', '范式,传递依赖', '数据库设计', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '1NF', 0, 1), (LAST_INSERT_ID(), 'B', '2NF', 1, 2), (LAST_INSERT_ID(), 'C', '3NF', 0, 3), (LAST_INSERT_ID(), 'D', 'BCNF', 0, 4);

-- S4: SQL连接
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 1, 'LEFT JOIN和INNER JOIN的区别是？', 2, 2, 'B', 'LEFT JOIN保留左表所有记录，INNER JOIN只保留匹配记录', 'JOIN,连接查询', 'SQL基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '没有区别', 0, 1), (LAST_INSERT_ID(), 'B', 'LEFT JOIN保留左表所有记录', 1, 2), (LAST_INSERT_ID(), 'C', 'INNER JOIN保留左表所有记录', 0, 3), (LAST_INSERT_ID(), 'D', 'LEFT JOIN比INNER JOIN快', 0, 4);

-- S5: 索引
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 1, '以下哪种情况不适合建立索引？', 3, 2, 'D', '频繁更新的列不适合建立索引，因为维护索引的代价很高', '索引', '索引优化', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '主键列', 0, 1), (LAST_INSERT_ID(), 'B', 'WHERE条件常用列', 0, 2), (LAST_INSERT_ID(), 'C', '外键列', 0, 3), (LAST_INSERT_ID(), 'D', '频繁更新的列', 1, 4);

-- S6: 视图
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 1, '关于数据库视图，以下说法正确的是？', 2, 2, 'A', '视图是虚拟表，不存储实际数据，数据来源于基表', '视图', 'SQL基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '视图不存储实际数据', 1, 1), (LAST_INSERT_ID(), 'B', '视图会复制一份基表数据', 0, 2), (LAST_INSERT_ID(), 'C', '视图不能进行查询操作', 0, 3), (LAST_INSERT_ID(), 'D', '视图只能基于单表创建', 0, 4);

-- S7: 锁
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 1, 'MySQL InnoDB存储引擎使用的锁粒度是？', 3, 2, 'B', 'InnoDB支持行级锁，MyISAM只支持表级锁', '锁,InnoDB', '事务', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '表级锁', 0, 1), (LAST_INSERT_ID(), 'B', '行级锁', 1, 2), (LAST_INSERT_ID(), 'C', '页级锁', 0, 3), (LAST_INSERT_ID(), 'D', '数据库级锁', 0, 4);

-- S8: SQL语句执行顺序
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 1, 'SQL语句中，以下哪个子句最先执行？', 3, 2, 'A', 'SQL执行顺序：FROM -> WHERE -> GROUP BY -> HAVING -> SELECT -> ORDER BY', 'SQL执行顺序', 'SQL进阶', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'FROM', 1, 1), (LAST_INSERT_ID(), 'B', 'WHERE', 0, 2), (LAST_INSERT_ID(), 'C', 'SELECT', 0, 3), (LAST_INSERT_ID(), 'D', 'GROUP BY', 0, 4);

-- S9: 数据库备份
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 1, '以下哪种备份方式备份速度最快但恢复最慢？', 3, 2, 'A', '增量备份只备份变化的数据，速度快；但恢复时需要所有增量备份链', '备份', '数据库管理', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '增量备份', 1, 1), (LAST_INSERT_ID(), 'B', '全量备份', 0, 2), (LAST_INSERT_ID(), 'C', '差异备份', 0, 3), (LAST_INSERT_ID(), 'D', '以上都一样', 0, 4);

-- S10: E-R模型
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 1, '在E-R模型中，菱形表示？', 1, 2, 'B', 'E-R模型中：矩形表示实体，菱形表示联系，椭圆形表示属性', 'E-R模型', '数据库设计', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '实体', 0, 1), (LAST_INSERT_ID(), 'B', '联系', 1, 2), (LAST_INSERT_ID(), 'C', '属性', 0, 3), (LAST_INSERT_ID(), 'D', '主键', 0, 4);

-- ==================== 二、多选题 (5题) ====================

-- M1: SQL约束
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 2, '以下哪些是SQL的完整性约束？', 1, 4, 'A,B,C', 'PRIMARY KEY、FOREIGN KEY、CHECK都是完整性约束，INDEX是索引不是约束', '约束', 'SQL基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'PRIMARY KEY', 1, 1), (LAST_INSERT_ID(), 'B', 'FOREIGN KEY', 1, 2), (LAST_INSERT_ID(), 'C', 'CHECK', 1, 3), (LAST_INSERT_ID(), 'D', 'INDEX', 0, 4);

-- M2: 事务特性
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 2, '事务的ACID特性包括以下哪些？', 1, 4, 'A,B,C', 'ACID: Atomicity(原子性)、Consistency(一致性)、Isolation(隔离性)、Durability(持久性)', 'ACID,事务', '事务', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '原子性(Atomicity)', 1, 1), (LAST_INSERT_ID(), 'B', '一致性(Consistency)', 1, 2), (LAST_INSERT_ID(), 'C', '隔离性(Isolation)', 1, 3), (LAST_INSERT_ID(), 'D', '可用性(Availability)', 0, 4);

-- M3: 关系代数
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 2, '以下哪些是关系代数的基本操作？', 3, 4, 'A,B,D', '选择、投影、并是基本操作，连接可以用基本操作推导', '关系代数', '关系理论', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '选择(Selection)', 1, 1), (LAST_INSERT_ID(), 'B', '投影(Projection)', 1, 2), (LAST_INSERT_ID(), 'C', '连接(Join)', 0, 3), (LAST_INSERT_ID(), 'D', '并(Union)', 1, 4);

-- M4: MySQL存储引擎
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 2, 'MySQL中InnoDB相对于MyISAM的优势包括？', 3, 4, 'A,B,C', 'InnoDB支持事务、行级锁和外键，MyISAM不支持', 'InnoDB,MyISAM', 'MySQL', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '支持事务', 1, 1), (LAST_INSERT_ID(), 'B', '支持行级锁', 1, 2), (LAST_INSERT_ID(), 'C', '支持外键', 1, 3), (LAST_INSERT_ID(), 'D', '支持全文索引', 0, 4);

-- M5: SQL操作
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 2, '以下哪些SQL语句属于DML(数据操纵语言)？', 2, 4, 'A,B,C', 'SELECT/INSERT/UPDATE/DELETE属于DML，CREATE属于DDL', 'DML,DDL', 'SQL基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'SELECT', 1, 1), (LAST_INSERT_ID(), 'B', 'INSERT', 1, 2), (LAST_INSERT_ID(), 'C', 'UPDATE', 1, 3), (LAST_INSERT_ID(), 'D', 'CREATE', 0, 4);

-- ==================== 三、判断题 (5题) ====================

-- T1
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 3, '一个表只能有一个主键', 1, 2, 'T', '一个表只能有一个PRIMARY KEY约束，但可以有多个UNIQUE约束', '主键', 'SQL基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 1, 1), (LAST_INSERT_ID(), 'B', '错误', 0, 2);

-- T2
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 3, '数据库三级模式中外模式对应用户视图', 2, 2, 'T', '外模式(用户模式)对应用户视图，模式(概念模式)对应全局逻辑结构，内模式对应物理存储', '三级模式', '数据库原理', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 1, 1), (LAST_INSERT_ID(), 'B', '错误', 0, 2);

-- T3
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 3, 'HAVING子句可以不与GROUP BY一起使用', 2, 2, 'T', '在MySQL中HAVING可以单独使用，此时对整个结果集进行筛选', 'HAVING,GROUP BY', 'SQL进阶', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 1, 1), (LAST_INSERT_ID(), 'B', '错误', 0, 2);

-- T4
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 3, 'B+树索引适合范围查询', 2, 2, 'T', 'B+树叶节点通过链表连接，支持高效的范围扫描和排序', 'B+树,索引', '索引优化', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 1, 1), (LAST_INSERT_ID(), 'B', '错误', 0, 2);

-- T5
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (2, 3, '数据库规范化程度越高，查询性能一定越好', 3, 2, 'F', '规范化减少冗余但可能增加连接操作，有时反规范化反而提升查询性能', '规范化,性能', '数据库设计', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 0, 1), (LAST_INSERT_ID(), 'B', '错误', 1, 2);

-- ==================== 四、填空题 (5题，无选项) ====================

INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id) VALUES
(2, 4, 'SQL中用于修改表结构的关键字是____', 1, 2, 'ALTER', 'ALTER TABLE用于修改表结构，如添加/删除/修改列', 'DDL,ALTER', 'SQL基础', 1, 4),
(2, 4, '关系数据库中，能够唯一标识一行数据的属性或属性组称为____', 1, 2, '候选键', '候选键是能唯一标识元组的最小属性集，被选定的候选键称为主键', '候选键,主键', '关系理论', 1, 4),
(2, 4, 'MySQL中，____存储引擎支持事务处理', 2, 2, 'InnoDB', 'InnoDB是MySQL默认的事务型存储引擎，支持ACID事务', 'InnoDB,存储引擎', 'MySQL', 1, 4),
(2, 4, '数据库的三级模式结构包括外模式、____和内模式', 2, 2, '模式', '三级模式：外模式(用户视图)、模式(全局逻辑结构)、内模式(物理存储)', '三级模式', '数据库原理', 1, 4),
(2, 4, 'SQL中____关键字用于对分组后的结果进行筛选', 2, 2, 'HAVING', 'HAVING对GROUP BY分组后的结果进行条件筛选，WHERE对分组前的数据筛选', 'HAVING,GROUP BY', 'SQL进阶', 1, 4);

-- ==================== 五、简答题 (5题，无选项) ====================

INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id) VALUES
(2, 5, '请简述数据库事务的ACID特性', 2, 5, 'A原子性、C一致性、I隔离性、D持久性', '1)原子性：事务中的操作要么全部执行，要么全部不执行；2)一致性：事务前后数据库从一个一致状态到另一个一致状态；3)隔离性：并发事务之间互不干扰；4)持久性：事务提交后对数据的改变是永久的', 'ACID,事务', '事务', 1, 4),
(2, 5, '请简述数据库三大范式的含义', 2, 5, '1NF要求属性不可再分，2NF消除部分依赖，3NF消除传递依赖', '第一范式(1NF)：关系中每个属性值都是不可再分的原子值。第二范式(2NF)：在1NF基础上，非主属性完全依赖于候选键(消除部分依赖)。第三范式(3NF)：在2NF基础上，非主属性不传递依赖于候选键(消除传递依赖)', '范式,数据库设计', '数据库设计', 1, 4),
(2, 5, '请简述索引的作用及其优缺点', 3, 5, '索引加快查询速度，但占用存储空间且降低写入性能', '优点：1)大大加快数据检索速度；2)减少查询中排序和分组的时间。缺点：1)占用额外的存储空间；2)降低INSERT/UPDATE/DELETE的执行速度(需要维护索引)；3)过多索引会增加优化器选择的开销', '索引', '索引优化', 1, 4),
(2, 5, '请简述MySQL中InnoDB和MyISAM的区别', 3, 5, 'InnoDB支持事务和行级锁，MyISAM不支持事务但查询速度快', '主要区别：1)InnoDB支持事务，MyISAM不支持；2)InnoDB支持行级锁，MyISAM只支持表级锁；3)InnoDB支持外键，MyISAM不支持；4)InnoDB支持MVCC，MyISAM不支持；5)MyISAM查询速度相对较快；6)InnoDB支持崩溃恢复', 'InnoDB,MyISAM', 'MySQL', 1, 4),
(2, 5, '请简述SQL注入的原理及防范措施', 4, 5, 'SQL注入是通过拼接恶意SQL到查询参数中执行非预期操作，使用参数化查询可防范', '原理：应用程序将用户输入直接拼接到SQL语句中，攻击者可构造恶意输入改变SQL语义。防范措施：1)使用参数化查询(PreparedStatement)；2)使用ORM框架；3)输入验证和过滤；4)最小权限原则；5)使用存储过程', 'SQL注入,安全', '数据库安全', 1, 4);

SELECT '数据库题库扩展完成：单选10题 + 多选5题 + 判断5题 + 填空5题 + 简答5题 = 共30题' AS result;
