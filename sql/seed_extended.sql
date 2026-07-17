-- ========== 扩展种子数据 ==========
-- 更多班级、学生、考试数据，用于展示完整功能
-- 密码均为: 123456 (BCrypt hash)

-- =============================================
-- 1. 扩展班级数据 (15个班级，跨3个学院)
-- =============================================
INSERT INTO sys_class (class_name, grade_name, dept_name, status) VALUES
-- 计算机学院 2022级
('软件工程2201', '2022级', '计算机学院', 1),
('软件工程2202', '2022级', '计算机学院', 1),
('计算机科学2201', '2022级', '计算机学院', 1),
('网络工程2201', '2022级', '计算机学院', 1),
('人工智能2201', '2022级', '计算机学院', 1),
-- 计算机学院 2023级
('软件工程2301', '2023级', '计算机学院', 1),
('计算机科学2301', '2023级', '计算机学院', 1),
('大数据2301', '2023级', '计算机学院', 1),
-- 电子信息学院 2022级
('通信工程2201', '2022级', '电子信息学院', 1),
('电子信息2201', '2022级', '电子信息学院', 1),
('物联网2201', '2022级', '电子信息学院', 1),
-- 电子信息学院 2023级
('通信工程2301', '2023级', '电子信息学院', 1),
('自动化2301', '2023级', '电子信息学院', 1),
-- 数学学院 2022级
('数学与应用数学2201', '2022级', '数学学院', 1),
('统计学2201', '2022级', '数学学院', 1);

-- =============================================
-- 2. 扩展教师账号 (8个，分属不同学院)
-- =============================================
INSERT INTO sys_user (username, password, real_name, user_type, teacher_no, dept_name, phone, status) VALUES
-- 计算机学院教师
('zhangwei', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '张伟', 2, 'T2022001', '计算机学院', '13800003001', 1),
('lina', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '李娜', 2, 'T2022002', '计算机学院', '13800003002', 1),
('wangfang', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '王芳', 2, 'T2022003', '计算机学院', '13800003003', 1),
-- 电子信息学院教师
('liuyang', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '刘洋', 2, 'T2022004', '电子信息学院', '13800003004', 1),
('chenjie', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '陈杰', 2, 'T2022005', '电子信息学院', '13800003005', 1),
-- 数学学院教师
('zhaoli', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '赵丽', 2, 'T2022006', '数学学院', '13800003006', 1),
('sunqiang', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '孙强', 2, 'T2022007', '数学学院', '13800003007', 1),
('zhoumin', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '周敏', 2, 'T2022008', '数学学院', '13800003008', 1);

-- 为新教师分配角色
INSERT INTO sys_user_role (user_id, role_id) SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username IN ('zhangwei','lina','wangfang','liuyang','chenjie','zhaoli','sunqiang','zhoumin') AND r.role_code='TEACHER';

-- =============================================
-- 3. 扩展学生账号 (60个，分布在不同班级)
-- =============================================
INSERT INTO sys_user (username, password, real_name, user_type, student_no, dept_name, class_id, phone, status) VALUES
-- 软件工程2201 (class_id=6, 8人)
('stu220101', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '张三丰', 3, 'S2022001', '计算机学院', 6, '13900010001', 1),
('stu220102', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '李思思', 3, 'S2022002', '计算机学院', 6, '13900010002', 1),
('stu220103', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '王小明', 3, 'S2022003', '计算机学院', 6, '13900010003', 1),
('stu220104', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '赵晓燕', 3, 'S2022004', '计算机学院', 6, '13900010004', 1),
('stu220105', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '刘德华', 3, 'S2022005', '计算机学院', 6, '13900010005', 1),
('stu220106', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '周杰伦', 3, 'S2022006', '计算机学院', 6, '13900010006', 1),
('stu220107', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '吴彦祖', 3, 'S2022007', '计算机学院', 6, '13900010007', 1),
('stu220108', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '郑秀文', 3, 'S2022008', '计算机学院', 6, '13900010008', 1),

-- 软件工程2202 (class_id=7, 7人)
('stu220201', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '孙悟空', 3, 'S2022009', '计算机学院', 7, '13900010009', 1),
('stu220202', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '猪八戒', 3, 'S2022010', '计算机学院', 7, '13900010010', 1),
('stu220203', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '沙悟净', 3, 'S2022011', '计算机学院', 7, '13900010011', 1),
('stu220204', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '唐三藏', 3, 'S2022012', '计算机学院', 7, '13900010012', 1),
('stu220205', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '白龙马', 3, 'S2022013', '计算机学院', 7, '13900010013', 1),
('stu220206', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '观音菩萨', 3, 'S2022014', '计算机学院', 7, '13900010014', 1),
('stu220207', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '如来佛祖', 3, 'S2022015', '计算机学院', 7, '13900010015', 1),

-- 计算机科学2201 (class_id=8, 6人)
('stu220301', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '诸葛亮', 3, 'S2022016', '计算机学院', 8, '13900010016', 1),
('stu220302', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '刘备', 3, 'S2022017', '计算机学院', 8, '13900010017', 1),
('stu220303', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '关羽', 3, 'S2022018', '计算机学院', 8, '13900010018', 1),
('stu220304', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '张飞', 3, 'S2022019', '计算机学院', 8, '13900010019', 1),
('stu220305', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '赵云', 3, 'S2022020', '计算机学院', 8, '13900010020', 1),
('stu220306', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '黄忠', 3, 'S2022021', '计算机学院', 8, '13900010021', 1),

-- 网络工程2201 (class_id=9, 5人)
('stu220401', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '李白', 3, 'S2022022', '计算机学院', 9, '13900010022', 1),
('stu220402', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '杜甫', 3, 'S2022023', '计算机学院', 9, '13900010023', 1),
('stu220403', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '白居易', 3, 'S2022024', '计算机学院', 9, '13900010024', 1),
('stu220404', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '王维', 3, 'S2022025', '计算机学院', 9, '13900010025', 1),
('stu220405', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '孟浩然', 3, 'S2022026', '计算机学院', 9, '13900010026', 1),

-- 人工智能2201 (class_id=10, 6人)
('stu220501', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '爱因斯坦', 3, 'S2022027', '计算机学院', 10, '13900010027', 1),
('stu220502', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '牛顿', 3, 'S2022028', '计算机学院', 10, '13900010028', 1),
('stu220503', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '霍金', 3, 'S2022029', '计算机学院', 10, '13900010029', 1),
('stu220504', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '居里夫人', 3, 'S2022030', '计算机学院', 10, '13900010030', 1),
('stu220505', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '达尔文', 3, 'S2022031', '计算机学院', 10, '13900010031', 1),
('stu220506', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '特斯拉', 3, 'S2022032', '计算机学院', 10, '13900010032', 1),

-- 通信工程2201 (class_id=11, 6人，电子信息学院)
('stu220601', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '钱学森', 3, 'S2022033', '电子信息学院', 11, '13900010033', 1),
('stu220602', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '邓稼先', 3, 'S2022034', '电子信息学院', 11, '13900010034', 1),
('stu220603', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '华罗庚', 3, 'S2022035', '电子信息学院', 11, '13900010035', 1),
('stu220604', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '陈景润', 3, 'S2022036', '电子信息学院', 11, '13900010036', 1),
('stu220605', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '苏步青', 3, 'S2022037', '电子信息学院', 11, '13900010037', 1),
('stu220606', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '谷超豪', 3, 'S2022038', '电子信息学院', 11, '13900010038', 1),

-- 电子信息2201 (class_id=12, 5人)
('stu220701', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '杨振宁', 3, 'S2022039', '电子信息学院', 12, '13900010039', 1),
('stu220702', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '李政道', 3, 'S2022040', '电子信息学院', 12, '13900010040', 1),
('stu220703', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '丁肇中', 3, 'S2022041', '电子信息学院', 12, '13900010041', 1),
('stu220704', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '朱棣文', 3, 'S2022042', '电子信息学院', 12, '13900010042', 1),
('stu220705', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '崔琦', 3, 'S2022043', '电子信息学院', 12, '13900010043', 1),

-- 物联网2201 (class_id=13, 5人)
('stu220801', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '马云', 3, 'S2022044', '电子信息学院', 13, '13900010044', 1),
('stu220802', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '马化腾', 3, 'S2022045', '电子信息学院', 13, '13900010045', 1),
('stu220803', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '任正非', 3, 'S2022046', '电子信息学院', 13, '13900010046', 1),
('stu220804', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '雷军', 3, 'S2022047', '电子信息学院', 13, '13900010047', 1),
('stu220805', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '刘强东', 3, 'S2022048', '电子信息学院', 13, '13900010048', 1),

-- 数学与应用数学2201 (class_id=16, 5人，数学学院)
('stu220901', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '高斯', 3, 'S2022049', '数学学院', 16, '13900010049', 1),
('stu220902', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '欧拉', 3, 'S2022050', '数学学院', 16, '13900010050', 1),
('stu220903', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '黎曼', 3, 'S2022051', '数学学院', 16, '13900010051', 1),
('stu220904', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '希尔伯特', 3, 'S2022052', '数学学院', 16, '13900010052', 1),
('stu220905', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '柯西', 3, 'S2022053', '数学学院', 16, '13900010053', 1),

-- 统计学2201 (class_id=17, 6人)
('stu221001', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '费马', 3, 'S2022054', '数学学院', 17, '13900010054', 1),
('stu221002', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '帕斯卡', 3, 'S2022055', '数学学院', 17, '13900010055', 1),
('stu221003', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '拉普拉斯', 3, 'S2022056', '数学学院', 17, '13900010056', 1),
('stu221004', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '傅里叶', 3, 'S2022057', '数学学院', 17, '13900010057', 1),
('stu221005', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '拉格朗日', 3, 'S2022058', '数学学院', 17, '13900010058', 1),
('stu221006', '$2a$10$d4vz2JrAG.dtAerkkFTTaOQP3/VXUr9oylwBDXTrcQ737yVeFDs9C', '诺特', 3, 'S2022059', '数学学院', 17, '13900010059', 1);

-- 为新学生分配角色
INSERT INTO sys_user_role (user_id, role_id) SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username LIKE 'stu22%' AND r.role_code='STUDENT';

-- =============================================
-- 4. 扩展题库分类
-- =============================================
INSERT INTO question_category (parent_id, category_name, sort_no, status, create_by) VALUES
(0, '高等数学', 7, 1, 1),
(0, '线性代数', 8, 1, 1),
(0, '概率统计', 9, 1, 1),
(0, '通信原理', 10, 1, 1),
(0, '信号与系统', 11, 1, 1),
(0, '模拟电子技术', 12, 1, 1);

-- =============================================
-- 5. 扩展题库 (高等数学 15题)
-- =============================================
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id) VALUES
-- 高等数学单选 (5题)
(7, 1, '极限lim(x→0)sin(x)/x等于？', 1, 2, 'A', '这是重要极限，结果为1', '极限', '高数,基础', 1, 4),
(7, 1, '函数f(x)=x²在x=1处的导数是？', 1, 2, 'B', 'f\'(x)=2x，f\'(1)=2', '导数', '高数,基础', 1, 4),
(7, 1, '∫x dx等于？', 1, 2, 'C', '∫x dx = x²/2 + C', '积分', '高数,基础', 1, 4),
(7, 1, '以下哪个函数在x=0处连续？', 2, 2, 'A', 'x²在x=0处连续且可导', '连续性', '高数', 1, 4),
(7, 1, '微分方程y\'=y的通解是？', 3, 2, 'B', '分离变量得y=Ce^x', '微分方程', '高数,进阶', 1, 4),
-- 高等数学判断 (5题)
(7, 3, '连续函数一定可导', 2, 3, 'F', '反例：y=|x|在x=0连续但不可导', '连续与可导', '高数', 1, 4),
(7, 3, '可导函数一定连续', 1, 3, 'T', '可导必连续，连续不一定可导', '连续与可导', '高数', 1, 4),
(7, 3, '无穷小量乘以有界变量仍是无穷小', 2, 3, 'T', '无穷小性质', '无穷小', '高数', 1, 4),
(7, 3, '定积分的值一定是正数', 3, 3, 'F', '定积分可正可负可零', '定积分', '高数', 1, 4),
(7, 3, '二阶导数大于0表示函数是凹函数', 2, 3, 'T', '二阶导数判断凹凸性', '凹凸性', '高数', 1, 4),
-- 高等数学简答 (5题)
(7, 5, '简述罗尔定理的内容', 2, 5, 'f(a)=f(b)则存在c使f\'(c)=0', '闭区间连续，开区间可导，端点值相等', '中值定理', '高数', 1, 4),
(7, 5, '什么是洛必达法则？', 2, 5, '0/0或∞/∞型极限可用导数比值求极限', '适用于0/0和∞/∞未定式', '极限', '高数', 1, 4),
(7, 5, '解释定积分的几何意义', 1, 5, '曲边梯形的面积', '定积分表示曲线与x轴围成的面积', '积分', '高数', 1, 4),
(7, 5, '什么是函数的极值点？', 1, 5, '导数为0且左右导数变号的点', '极值点是局部最大或最小值点', '极值', '高数', 1, 4),
(7, 5, '简述牛顿-莱布尼茨公式', 2, 5, '∫[a,b]f(x)dx=F(b)-F(a)', '连接定积分与不定积分的桥梁', '积分', '高数', 1, 4);

-- 高等数学选项
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(29,'A','1',1,1),(29,'B','0',0,2),(29,'C','∞',0,3),(29,'D','不存在',0,4),
(30,'A','1',0,1),(30,'B','2',1,2),(30,'C','0',0,3),(30,'D','x²',0,4),
(31,'A','x+C',0,1),(31,'B','1+C',0,2),(31,'C','x²/2+C',1,3),(31,'D','2x+C',0,4),
(32,'A','x²',1,1),(32,'B','1/x',0,2),(32,'C','|x|',0,3),(32,'D','tan(x)',0,4),
(33,'A','y=x+C',0,1),(33,'B','y=Ce^x',1,2),(33,'C','y=e^x',0,3),(33,'D','y=Cx',0,4);

-- =============================================
-- 6. 扩展试卷 (4份)
-- =============================================
INSERT INTO paper (paper_name, paper_type, total_score, question_count, duration_minutes, status, creator_id) VALUES
('高等数学期末试卷A', 1, 100, 20, 120, 1, 4),
('高等数学期末试卷B', 1, 100, 20, 120, 1, 5),
('通信原理期中试卷', 1, 60, 10, 90, 1, 14),
('概率统计测验', 1, 50, 10, 60, 1, 15);

-- 试卷题目关联 (高等数学试卷A: 20题)
INSERT INTO paper_question (paper_id, question_id, question_type, question_order, score, question_snapshot)
SELECT 3, id, question_type, ROW_NUMBER() OVER (ORDER BY id), score,
    CONCAT('{"sourceQuestionId":',id,',"questionType":',question_type,',"title":"',REPLACE(title,'"','\\"'),'","difficulty":',difficulty,',"score":',score,',"correctAnswer":"',IFNULL(correct_answer,''),'","answerAnalysis":"',IFNULL(answer_analysis,''),'","tags":"',IFNULL(tags,''),'"}')
FROM question WHERE category_id=7 LIMIT 20;

-- 高等数学试卷B: 复用题目
INSERT INTO paper_question (paper_id, question_id, question_type, question_order, score, question_snapshot)
SELECT 4, id, question_type, ROW_NUMBER() OVER (ORDER BY id), score,
    CONCAT('{"sourceQuestionId":',id,',"questionType":',question_type,',"title":"',REPLACE(title,'"','\\"'),'","difficulty":',difficulty,',"score":',score,',"correctAnswer":"',IFNULL(correct_answer,''),'","answerAnalysis":"',IFNULL(answer_analysis,''),'","tags":"',IFNULL(tags,''),'"}')
FROM question WHERE category_id=7 LIMIT 20;

-- =============================================
-- 7. 扩展考试 (6场)
-- =============================================
INSERT INTO exam (exam_name, paper_id, exam_mode, publish_status, start_time, end_time, duration_minutes, total_score, pass_score, anti_cheat_switch, cut_screen_limit, auto_submit_switch, creator_id) VALUES
('高等数学期末考试A卷', 3, 1, 1, '2026-05-01 08:00:00', '2026-05-20 23:59:00', 120, 100, 60, 1, 3, 1, 4),
('高等数学期末考试B卷', 4, 1, 1, '2026-05-01 08:00:00', '2026-05-20 23:59:00', 120, 100, 60, 1, 3, 1, 5),
('Java基础补考', 1, 1, 1, '2026-05-10 14:00:00', '2026-05-15 18:00:00', 120, 100, 60, 0, 5, 1, 4),
('2022级Java期中测验', 1, 1, 2, '2026-04-01 09:00:00', '2026-04-01 11:00:00', 120, 100, 60, 1, 3, 1, 4),
('计算机网络补考', 2, 1, 1, '2026-05-12 09:00:00', '2026-05-18 23:59:00', 90, 60, 36, 0, 5, 1, 5),
('软件工程2201班内测验', 1, 1, 0, '2026-05-20 14:00:00', '2026-05-20 16:00:00', 60, 50, 30, 0, 3, 1, 6);

-- =============================================
-- 8. 考试分配 (exam_paper) - 多样化分配
-- =============================================

-- 高等数学期末考试A卷 - 分配给计算机学院2022级学生
INSERT INTO exam_paper (exam_id, paper_id, student_id, answer_status, start_time, device_info)
SELECT 3, 3, id, 0, NULL, '' FROM sys_user WHERE user_type=3 AND class_id IN (6,7,8,9,10);

-- 高等数学期末考试B卷 - 分配给电子信息学院和数学学院学生
INSERT INTO exam_paper (exam_id, paper_id, student_id, answer_status, start_time, device_info)
SELECT 4, 4, id, 0, NULL, '' FROM sys_user WHERE user_type=3 AND class_id IN (11,12,13,16,17);

-- Java基础补考 - 分配给部分需要补考的学生
INSERT INTO exam_paper (exam_id, paper_id, student_id, answer_status, start_time, device_info)
SELECT 5, 1, id, 0, NULL, '' FROM sys_user WHERE user_type=3 AND username IN ('stu220101','stu220102','stu220201','stu220301','stu220401','stu220501','stu220601','stu220701','stu220801','stu220901','stu221001');

-- 2022级Java期中测验 (已结束) - 全体2022级学生，部分已提交
INSERT INTO exam_paper (exam_id, paper_id, student_id, answer_status, start_time, submit_time, total_score, used_minutes, device_info)
SELECT 6, 1, id, 2, '2026-04-01 09:00:00', '2026-04-01 10:30:00', FLOOR(RAND()*40+60), FLOOR(RAND()*60+30), 'Chrome/120.0'
FROM sys_user WHERE user_type=3 AND class_id IN (6,7,8,9,10);

-- 计算机网络补考 - 分配给部分学生
INSERT INTO exam_paper (exam_id, paper_id, student_id, answer_status, start_time, device_info)
SELECT 7, 2, id, 0, NULL, '' FROM sys_user WHERE user_type=3 AND username IN ('stu220103','stu220202','stu220302','stu220402','stu220502','stu220602','stu220702','stu220802','stu220902','stu221002');

-- 软件工程2201班内测验 - 只分配给本班
INSERT INTO exam_paper (exam_id, paper_id, student_id, answer_status, start_time, device_info)
SELECT 8, 1, id, 0, NULL, '' FROM sys_user WHERE user_type=3 AND class_id = 6;

-- =============================================
-- 9. 模拟已提交的考试记录 (部分学生已完成考试)
-- =============================================

-- 为高等数学期末考试A卷部分学生添加已提交记录
UPDATE exam_paper SET answer_status = 2, start_time = '2026-05-10 09:00:00', submit_time = '2026-05-10 10:45:00',
    total_score = FLOOR(RAND()*30+70), used_minutes = FLOOR(RAND()*60+60), objective_score = FLOOR(RAND()*20+40),
    subjective_score = FLOOR(RAND()*20+30)
WHERE exam_id = 3 AND student_id IN (SELECT id FROM sys_user WHERE username IN ('stu220101','stu220102','stu220103','stu220201','stu220202','stu220301','stu220302','stu220401','stu220501','stu220502'));

-- 为高等数学期末考试B卷部分学生添加已提交记录
UPDATE exam_paper SET answer_status = 2, start_time = '2026-05-10 14:00:00', submit_time = '2026-05-10 15:50:00',
    total_score = FLOOR(RAND()*30+65), used_minutes = FLOOR(RAND()*50+60), objective_score = FLOOR(RAND()*20+35),
    subjective_score = FLOOR(RAND()*20+30)
WHERE exam_id = 4 AND student_id IN (SELECT id FROM sys_user WHERE username IN ('stu220601','stu220602','stu220701','stu220702','stu220801','stu220901','stu220902','stu221001','stu221002','stu221003'));

-- 为部分学生添加已批改记录
UPDATE exam_paper SET answer_status = 3 WHERE exam_id = 3 AND student_id IN (SELECT id FROM sys_user WHERE username IN ('stu220101','stu220102','stu220201','stu220301','stu220401'));
UPDATE exam_paper SET answer_status = 3 WHERE exam_id = 4 AND student_id IN (SELECT id FROM sys_user WHERE username IN ('stu220601','stu220701','stu220801','stu220901','stu221001'));

SELECT 'Extended seed data inserted successfully' AS result;
