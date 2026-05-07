-- ========== 在线考试系统 种子数据 ==========
-- 密码均为: 密码123 (BCrypt hash)

-- 1. 班级数据 (5个班级)
INSERT INTO sys_class (class_name, grade_name, dept_name, status) VALUES
('软件工程2101', '2021级', '计算机学院', 1),
('软件工程2102', '2021级', '计算机学院', 1),
('计算机科学2101', '2021级', '计算机学院', 1),
('网络工程2101', '2021级', '计算机学院', 1),
('人工智能2101', '2021级', '计算机学院', 1);

-- 2. 教师账号 (3个)
INSERT INTO sys_user (username, password, real_name, user_type, teacher_no, dept_name, phone, status) VALUES
('wang', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '王建国', 2, 'T2023001', '计算机学院', '13800001001', 1),
('liu', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '刘芳', 2, 'T2023002', '计算机学院', '13800001002', 1),
('chen', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '陈明', 2, 'T2023003', '计算机学院', '13800001003', 1);

-- 为教师分配角色
INSERT INTO sys_user_role (user_id, role_id) SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username IN ('wang','liu','chen') AND r.role_code='TEACHER';

-- 3. 学生账号 (15个)
INSERT INTO sys_user (username, password, real_name, user_type, student_no, dept_name, class_id, phone, status) VALUES
('stu01', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '赵一', 3, 'S2024001', '计算机学院', 1, '13900002001', 1),
('stu02', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '钱二', 3, 'S2024002', '计算机学院', 1, '13900002002', 1),
('stu03', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '孙三', 3, 'S2024003', '计算机学院', 1, '13900002003', 1),
('stu04', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '李四', 3, 'S2024004', '计算机学院', 2, '13900002004', 1),
('stu05', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '周五', 3, 'S2024005', '计算机学院', 2, '13900002005', 1),
('stu06', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '吴六', 3, 'S2024006', '计算机学院', 2, '13900002006', 1),
('stu07', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '郑七', 3, 'S2024007', '计算机学院', 3, '13900002007', 1),
('stu08', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '王八', 3, 'S2024008', '计算机学院', 3, '13900002008', 1),
('stu09', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '冯九', 3, 'S2024009', '计算机学院', 3, '13900002009', 1),
('stu10', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '陈十', 3, 'S2024010', '计算机学院', 4, '13900002010', 1),
('stu11', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '褚十一', 3, 'S2024011', '计算机学院', 4, '13900002011', 1),
('stu12', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '卫十二', 3, 'S2024012', '计算机学院', 4, '13900002012', 1),
('stu13', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '蒋十三', 3, 'S2024013', '计算机学院', 5, '13900002013', 1),
('stu14', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '沈十四', 3, 'S2024014', '计算机学院', 5, '13900002014', 1),
('stu15', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '韩十五', 3, 'S2024015', '计算机学院', 5, '13900002015', 1);

INSERT INTO sys_user_role (user_id, role_id) SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username LIKE 'stu%' AND r.role_code='STUDENT';

-- 4. 题库分类 (6个)
INSERT INTO question_category (parent_id, category_name, sort_no, status, create_by) VALUES
(0, 'Java基础', 1, 1, 1), (0, '数据库', 2, 1, 1), (0, '计算机网络', 3, 1, 1),
(0, '数据结构', 4, 1, 1), (0, '操作系统', 5, 1, 1), (0, '软件工程', 6, 1, 1);

-- 5. 题库 (30道题)
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id) VALUES
-- Java单选 (5题)
(1, 1, 'Java中哪个关键字用于实现接口？', 1, 2, 'A', 'implements关键字用于实现接口，extends用于继承类', '接口,面向对象', 'Java基础,入门', 1, 4),
(1, 1, '以下哪个不是Java的基本数据类型？', 3, 2, 'C', 'String是引用类型，int/double/boolean是基本类型', '数据类型', 'Java基础', 1, 4),
(1, 1, 'Java程序的入口方法是？', 2, 2, 'B', 'main方法是Java程序的入口点', '程序结构', 'Java基础', 1, 4),
(1, 1, 'JVM代表什么？', 2, 2, 'A', 'JVM=Java Virtual Machine，是Java程序运行环境', 'JVM,虚拟机', 'Java基础', 1, 4),
(1, 1, '以下哪个包是Java默认导入的？', 4, 2, 'B', 'java.lang包是自动导入的', '包,import', 'Java基础', 1, 4),
-- Java多选 (3题)
(1, 2, '以下哪些是Java的访问修饰符？', 2, 4, 'A,B,C', 'public/protected/private是访问修饰符，static不是', '访问修饰符', 'Java基础', 1, 4),
(1, 2, '面向对象的三大特性包括？', 1, 4, 'A,B,C', '封装、继承、多态是面向对象三大特性', '面向对象', 'Java基础', 1, 4),
(1, 2, '以下哪些属于Java集合框架？', 3, 4, 'A,B,D', 'List/Map/Set是集合框架，String不是', '集合框架', 'Java基础', 1, 4),
-- Java判断 (4题)
(1, 3, 'Java中int默认值是0', 1, 3, 'T', 'int默认值为0', '默认值', 'Java基础', 1, 4),
(1, 3, 'Java支持多重继承', 2, 3, 'F', 'Java不支持多重继承，只能继承一个类', '继承', 'Java基础', 1, 4),
(1, 3, 'StringBuilder是线程安全的', 3, 3, 'F', 'StringBuffer是线程安全的，StringBuilder不是', '字符串,线程安全', 'Java基础', 1, 4),
(1, 3, 'Java中数组长度可以动态改变', 4, 3, 'F', 'Java数组长度在创建时就固定了', '数组', 'Java基础', 1, 4),
-- Java填空 (3题)
(1, 4, 'Java中用来创建对象的操作符是____', 2, 2, 'new', '使用new关键字创建对象实例', '对象创建', 'Java基础', 1, 4),
(1, 4, 'Java中表示父类引用的关键字是____', 2, 2, 'super', 'super用于访问父类成员', '继承', 'Java基础', 1, 4),
(1, 4, 'Java异常处理中使用____关键字声明可能抛出的异常', 3, 2, 'throws', 'throws声明在方法签名中', '异常处理', 'Java基础', 1, 4),
-- Java简答 (3题)
(1, 5, '简述Java中==和equals的区别', 3, 5, '==比较引用地址，equals比较内容', '==比较基本类型值和引用地址，equals默认比较引用(可重写比较内容)', '对象比较', 'Java基础', 1, 4),
(1, 5, '简述什么是Java垃圾回收机制', 2, 5, 'JVM自动回收不再使用的对象内存', 'GC是自动内存管理机制，回收堆中无引用对象', 'GC,内存', 'Java基础', 1, 4),
(1, 5, '简述面向对象中封装的好处', 1, 5, '隐藏实现细节，提高安全性和可维护性', '封装通过私有字段+公开方法控制访问', '封装', 'Java基础', 1, 4),
-- 数据库单选 (5题)
(2, 1, 'SQL中用于删除表中所有数据但保留表结构的命令是？', 2, 2, 'B', 'TRUNCATE快速删除所有行，DELETE逐行删除', 'SQL', '数据库', 1, 4),
(2, 1, '以下哪个不是关系型数据库？', 3, 2, 'C', 'MongoDB是NoSQL文档数据库，MySQL/PostgreSQL/Oracle是关系型', 'SQL,NoSQL', '数据库', 1, 4),
(2, 1, '数据库事务的ACID特性中，C代表什么？', 1, 2, 'A', 'Consistency=一致性，确保事务前后数据库状态一致', '事务,ACID', '数据库', 1, 4),
(2, 1, 'MySQL中用于创建索引的语句是？', 2, 2, 'B', 'CREATE INDEX用于创建索引', '索引', '数据库', 1, 4),
(2, 1, '什么是数据库范式化？', 4, 2, 'A', '范式化是减少数据冗余的过程', '范式,设计', '数据库', 1, 4),
-- 网络单选 (5题)
(3, 1, 'HTTP协议的默认端口是？', 1, 2, 'B', 'HTTP默认80端口，HTTPS默认443', 'HTTP,协议', '计算机网络', 1, 4),
(3, 1, 'TCP握手需要几次？', 2, 2, 'B', 'TCP三次握手建立连接', 'TCP,握手', '计算机网络', 1, 4),
(3, 1, 'IP地址127.0.0.1代表什么？', 1, 2, 'A', '127.0.0.1是本地回环地址', 'IP,网络', '计算机网络', 1, 4),
(3, 1, 'DNS的主要功能是？', 3, 2, 'A', 'DNS将域名解析为IP地址', 'DNS', '计算机网络', 1, 4),
(3, 1, 'OSI七层模型中传输层是哪层？', 3, 2, 'B', '传输层是第4层，物理/链路/网络/传输/会话/表示/应用', 'OSI,TCP', '计算机网络', 1, 4);

-- 题目选项
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
-- Java单选选项
(1,'A','implements',1,1),(1,'B','extends',0,2),(1,'C','abstract',0,3),(1,'D','interface',0,4),
(2,'A','int',0,1),(2,'B','double',0,2),(2,'C','String',1,3),(2,'D','boolean',0,4),
(3,'A','start',0,1),(3,'B','main',1,2),(3,'C','run',0,3),(3,'D','begin',0,4),
(4,'A','Java Virtual Machine',1,1),(4,'B','Java Variable Method',0,2),(4,'C','Java Very Modern',0,3),(4,'D','Java Version Manager',0,4),
(5,'A','java.util',0,1),(5,'B','java.lang',1,2),(5,'C','java.io',0,3),(5,'D','java.net',0,4),
-- Java多选选项
(6,'A','public',1,1),(6,'B','protected',1,2),(6,'C','private',1,3),(6,'D','static',0,4),
(7,'A','封装',1,1),(7,'B','继承',1,2),(7,'C','多态',1,3),(7,'D','并发',0,4),
(8,'A','List',1,1),(8,'B','Map',1,2),(8,'C','String',0,3),(8,'D','Set',1,4),
-- 判断选项 (每题2个)
(9,'A','正确',1,1),(9,'B','错误',0,2),
(10,'A','正确',0,1),(10,'B','错误',1,2),
(11,'A','正确',0,1),(11,'B','错误',1,2),
(12,'A','正确',0,1),(12,'B','错误',1,2),
-- 数据库单选选项
(19,'A','DROP',0,1),(19,'B','TRUNCATE',1,2),(19,'C','REMOVE',0,3),(19,'D','CLEAR',0,4),
(20,'A','MySQL',0,1),(20,'B','PostgreSQL',0,2),(20,'C','MongoDB',1,3),(20,'D','Oracle',0,4),
(21,'A','Consistency',1,1),(21,'B','Commit',0,2),(21,'C','Connection',0,3),(21,'D','Cache',0,4),
(22,'A','MAKE INDEX',0,1),(22,'B','CREATE INDEX',1,2),(22,'C','ADD INDEX',0,3),(22,'D','SET INDEX',0,4),
(23,'A','减少数据冗余',1,1),(23,'B','提高查询速度',0,2),(23,'C','增加数据安全',0,3),(23,'D','简化备份',0,4),
-- 网络单选选项
(24,'A','443',0,1),(24,'B','80',1,2),(24,'C','8080',0,3),(24,'D','3000',0,4),
(25,'A','2次',0,1),(25,'B','3次',1,2),(25,'C','4次',0,3),(25,'D','1次',0,4),
(26,'A','本机回环地址',1,1),(26,'B','网关地址',0,2),(26,'C','广播地址',0,3),(26,'D','DNS地址',0,4),
(27,'A','域名解析为IP',1,1),(27,'B','加密传输',0,2),(27,'C','分配IP',0,3),(27,'D','路由转发',0,4),
(28,'A','第3层',0,1),(28,'B','第4层',1,2),(28,'C','第5层',0,3),(28,'D','第2层',0,4);

-- 6. 试卷 (2份)
INSERT INTO paper (paper_name, paper_type, total_score, question_count, duration_minutes, status, creator_id) VALUES
('Java基础期末试卷', 1, 100, 15, 120, 1, 4),
('计算机网络期中试卷', 1, 60, 10, 90, 1, 4);

-- 试卷题目关联 (Java试卷: pick 15 questions from Java category)
INSERT INTO paper_question (paper_id, question_id, question_type, question_order, score, question_snapshot)
SELECT 1, id, question_type, (SELECT COUNT(*)+1 FROM question q2 WHERE q2.category_id=1 AND q2.id<=question.id), score,
    CONCAT('{"sourceQuestionId":',id,',"questionType":',question_type,',"title":"',REPLACE(title,'"','\\"'),'","difficulty":',difficulty,',"score":',score,',"correctAnswer":"',IFNULL(correct_answer,''),'","answerAnalysis":"',IFNULL(answer_analysis,''),'","tags":"',IFNULL(tags,''),'"}')
FROM question WHERE category_id=1 LIMIT 15;

-- 计算机网络试卷题目 (10题)
INSERT INTO paper_question (paper_id, question_id, question_type, question_order, score, question_snapshot)
SELECT 2, id, question_type, (SELECT COUNT(*)+1 FROM question q2 WHERE q2.category_id=3 AND q2.id<=question.id) + 15, score,
    CONCAT('{"sourceQuestionId":',id,',"questionType":',question_type,',"title":"',REPLACE(title,'"','\\"'),'","difficulty":',difficulty,',"score":',score,',"correctAnswer":"',IFNULL(correct_answer,''),'","answerAnalysis":"',IFNULL(answer_analysis,''),'","tags":"',IFNULL(tags,''),'"}')
FROM question WHERE category_id=3 LIMIT 10;

-- 7. 考试 (2场)
INSERT INTO exam (exam_name, paper_id, exam_mode, publish_status, start_time, end_time, duration_minutes, total_score, pass_score, anti_cheat_switch, cut_screen_limit, auto_submit_switch, creator_id) VALUES
('Java基础期末考试', 1, 1, 1, '2026-04-25 08:00:00', '2026-05-10 23:59:00', 120, 100, 60, 1, 3, 1, 4),
('计算机网络期中考试', 2, 1, 1, '2026-04-20 09:00:00', '2026-05-15 23:59:00', 90, 60, 36, 0, 5, 1, 4);

-- 8. 考试分配 (exam_paper for students)
INSERT INTO exam_paper (exam_id, paper_id, student_id, answer_status, start_time, device_info)
SELECT 1, 1, id, 0, NULL, '' FROM sys_user WHERE user_type=3 LIMIT 10;

INSERT INTO exam_paper (exam_id, paper_id, student_id, answer_status, start_time, device_info)
SELECT 2, 2, id, 0, NULL, '' FROM sys_user WHERE user_type=3 LIMIT 10;

SELECT 'Seed data inserted successfully' AS result;
