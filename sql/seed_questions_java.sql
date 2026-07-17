-- ========== Java 题库扩展脚本 ==========
-- 分类ID: 1 (Java基础)
-- 题型: 1单选 2多选 3判断 4填空 5简答
-- 难度: 1简单 2较易 3中等 4较难 5困难
-- creator_id: 4 (wang教师)
-- 本脚本采用逐题插入方式，确保选项关联正确的题目ID

-- ==================== 一、单选题 (10题) ====================

-- S1: 常量关键字
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 1, '以下哪个关键字用于定义常量？', 1, 2, 'C', 'final用于定义常量，const是C/C++关键字，static定义静态变量', '常量,关键字', 'Java基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'const', 0, 1), (LAST_INSERT_ID(), 'B', 'static', 0, 2), (LAST_INSERT_ID(), 'C', 'final', 1, 3), (LAST_INSERT_ID(), 'D', 'define', 0, 4);

-- S2: String不可变
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 1, 'Java中String类的对象是？', 2, 2, 'A', 'String是不可变对象(immutable)，每次修改都会创建新对象', 'String,不可变', 'Java基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '不可变对象', 1, 1), (LAST_INSERT_ID(), 'B', '可变对象', 0, 2), (LAST_INSERT_ID(), 'C', '基本类型', 0, 3), (LAST_INSERT_ID(), 'D', '接口', 0, 4);

-- S3: 线程安全集合
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 1, '以下哪个集合类是线程安全的？', 3, 2, 'B', 'Vector是线程安全的，ArrayList/LinkedList/HashSet都不是', '集合,线程安全', 'Java进阶', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'ArrayList', 0, 1), (LAST_INSERT_ID(), 'B', 'Vector', 1, 2), (LAST_INSERT_ID(), 'C', 'LinkedList', 0, 3), (LAST_INSERT_ID(), 'D', 'HashSet', 0, 4);

-- S4: finally return
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 1, 'try-catch-finally中，如果try和finally都有return语句，最终返回哪个？', 4, 2, 'C', 'finally中的return会覆盖try中的return，这是Java规范定义的行为', '异常处理,return', 'Java进阶', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'try中的return', 0, 1), (LAST_INSERT_ID(), 'B', '两者都返回', 0, 2), (LAST_INSERT_ID(), 'C', 'finally中的return', 1, 3), (LAST_INSERT_ID(), 'D', '编译错误', 0, 4);

-- S5: GC算法
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 1, '以下哪个不是Java的垃圾回收算法？', 3, 2, 'D', '引用计数法存在循环引用问题，Java没有采用', 'GC,内存管理', 'JVM', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '标记-清除', 0, 1), (LAST_INSERT_ID(), 'B', '复制算法', 0, 2), (LAST_INSERT_ID(), 'C', '标记-整理', 0, 3), (LAST_INSERT_ID(), 'D', '引用计数法', 1, 4);

-- S6: 抽象类vs接口
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 1, 'Java中抽象类和接口的区别，以下说法正确的是？', 3, 2, 'B', '抽象类可以有构造方法，接口不能有构造方法', '抽象类,接口', 'Java基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '接口可以有构造方法', 0, 1), (LAST_INSERT_ID(), 'B', '抽象类可以有构造方法', 1, 2), (LAST_INSERT_ID(), 'C', '两者都可以', 0, 3), (LAST_INSERT_ID(), 'D', '两者都不可以', 0, 4);

-- S7: HashMap默认容量
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 1, 'HashMap的默认初始容量是多少？', 2, 2, 'A', 'HashMap默认初始容量为16，负载因子为0.75', 'HashMap,集合', 'Java集合', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '16', 1, 1), (LAST_INSERT_ID(), 'B', '32', 0, 2), (LAST_INSERT_ID(), 'C', '8', 0, 3), (LAST_INSERT_ID(), 'D', '64', 0, 4);

-- S8: Spring注解
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 1, '以下哪个注解不是Spring框架提供的？', 3, 2, 'D', '@Entity是JPA注解，@Autowired/@Component/@Service都是Spring注解', 'Spring,注解', 'Spring框架', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '@Autowired', 0, 1), (LAST_INSERT_ID(), 'B', '@Component', 0, 2), (LAST_INSERT_ID(), 'C', '@Service', 0, 3), (LAST_INSERT_ID(), 'D', '@Entity', 1, 4);

-- S9: Comparable vs Comparator
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 1, 'Java中Comparable和Comparator的区别是？', 3, 2, 'A', 'Comparable在类内部实现(自然排序)，Comparator是外部比较器', '排序,比较器', 'Java进阶', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'Comparable在类内部实现', 1, 1), (LAST_INSERT_ID(), 'B', 'Comparator在类内部实现', 0, 2), (LAST_INSERT_ID(), 'C', '两者完全相同', 0, 3), (LAST_INSERT_ID(), 'D', 'Comparable是接口Comparator是类', 0, 4);

-- S10: 字节流
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 1, '以下哪个I/O类是字节流？', 2, 2, 'B', 'FileInputStream是字节流，BufferedReader/InputStreamReader/PrintWriter都是字符流', 'IO,字节流', 'Java IO', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'BufferedReader', 0, 1), (LAST_INSERT_ID(), 'B', 'FileInputStream', 1, 2), (LAST_INSERT_ID(), 'C', 'InputStreamReader', 0, 3), (LAST_INSERT_ID(), 'D', 'PrintWriter', 0, 4);

-- ==================== 二、多选题 (5题) ====================

-- M1: 引用类型
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 2, '以下哪些是Java的引用数据类型？', 2, 4, 'A,B,C', '数组、类、接口都是引用类型，int是基本数据类型', '数据类型,引用', 'Java基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '数组', 1, 1), (LAST_INSERT_ID(), 'B', '类', 1, 2), (LAST_INSERT_ID(), 'C', '接口', 1, 3), (LAST_INSERT_ID(), 'D', 'int', 0, 4);

-- M2: 类加载触发
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 2, '以下哪些情况会触发类的加载？', 3, 4, 'A,B,C', '创建实例、调用静态方法、访问静态字段都会触发类加载', '类加载,JVM', 'JVM', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '创建类的实例', 1, 1), (LAST_INSERT_ID(), 'B', '调用类的静态方法', 1, 2), (LAST_INSERT_ID(), 'C', '访问类的静态字段', 1, 3), (LAST_INSERT_ID(), 'D', '声明类的引用变量', 0, 4);

-- M3: final关键字
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 2, '关于final关键字，以下说法正确的是？', 3, 4, 'A,B,D', 'final类不能被继承，final方法不能被重写，final变量值不能改变', 'final,关键字', 'Java基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'final类不能被继承', 1, 1), (LAST_INSERT_ID(), 'B', 'final方法不能被重写', 1, 2), (LAST_INSERT_ID(), 'C', 'final方法不能被重载', 0, 3), (LAST_INSERT_ID(), 'D', 'final变量值不能改变', 1, 4);

-- M4: 设计模式
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 2, '以下哪些属于Java的设计模式？', 4, 4, 'A,C,D', '单例、观察者、工厂都是设计模式，递归是编程技巧不是设计模式', '设计模式', 'Java设计模式', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '单例模式', 1, 1), (LAST_INSERT_ID(), 'B', '递归', 0, 2), (LAST_INSERT_ID(), 'C', '观察者模式', 1, 3), (LAST_INSERT_ID(), 'D', '工厂模式', 1, 4);

-- M5: 线程安全集合
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 2, '以下哪些是线程安全的集合类？', 3, 4, 'A,B,D', 'ConcurrentHashMap/Vector/CopyOnWriteArrayList是线程安全的', '线程安全,集合', 'Java并发', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'ConcurrentHashMap', 1, 1), (LAST_INSERT_ID(), 'B', 'Vector', 1, 2), (LAST_INSERT_ID(), 'C', 'ArrayList', 0, 3), (LAST_INSERT_ID(), 'D', 'CopyOnWriteArrayList', 1, 4);

-- ==================== 三、判断题 (5题) ====================

-- T1
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 3, 'Java中所有类都直接或间接继承自Object类', 1, 2, 'T', 'Object是Java所有类的根类', '继承,Object', 'Java基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 1, 1), (LAST_INSERT_ID(), 'B', '错误', 0, 2);

-- T2
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 3, 'Java中static方法可以访问非static成员', 2, 2, 'F', 'static方法不能直接访问非static成员', 'static,关键字', 'Java基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 0, 1), (LAST_INSERT_ID(), 'B', '错误', 1, 2);

-- T3
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 3, 'ArrayList的查询时间复杂度是O(1)', 2, 2, 'T', 'ArrayList基于数组实现，通过索引查询是O(1)', 'ArrayList,复杂度', 'Java集合', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 1, 1), (LAST_INSERT_ID(), 'B', '错误', 0, 2);

-- T4
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 3, 'Java中的异常分为检查异常和非检查异常', 1, 2, 'T', '检查异常(checked)必须处理，非检查异常(unchecked)可以不处理', '异常处理', 'Java基础', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 1, 1), (LAST_INSERT_ID(), 'B', '错误', 0, 2);

-- T5
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (1, 3, 'synchronized修饰静态方法时锁的是当前对象实例', 3, 2, 'F', 'synchronized修饰静态方法时锁的是Class对象，不是实例对象', '多线程,synchronized', 'Java并发', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 0, 1), (LAST_INSERT_ID(), 'B', '错误', 1, 2);

-- ==================== 四、填空题 (5题，无选项) ====================

INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id) VALUES
(1, 4, 'Java中用于实现多线程的两种方式是继承____类和实现____接口', 2, 3, 'Thread,Runnable', '继承Thread类或实现Runnable接口是创建线程的两种基本方式', '多线程,Thread', 'Java并发', 1, 4),
(1, 4, 'Java集合框架的根接口是____和____', 2, 3, 'Collection,Map', 'Collection和Map是Java集合框架的两大根接口', '集合框架', 'Java集合', 1, 4),
(1, 4, 'JVM内存主要分为堆、栈、方法区、____和____', 3, 3, '程序计数器,本地方法栈', 'JVM运行时数据区包括堆、栈、方法区、程序计数器、本地方法栈', 'JVM内存', 'JVM', 1, 4),
(1, 4, 'Java中实现序列化需要实现____接口', 2, 2, 'Serializable', 'Serializable是标记接口，实现它即可让对象可序列化', '序列化', 'Java IO', 1, 4),
(1, 4, 'Spring框架的核心是____容器和____容器', 3, 3, 'BeanFactory,ApplicationContext', 'BeanFactory是基础容器，ApplicationContext是高级容器', 'Spring,IoC', 'Spring框架', 1, 4);

-- ==================== 五、简答题 (5题，无选项) ====================

INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id) VALUES
(1, 5, '请简述Java中重写(Override)和重载(Overload)的区别', 2, 5, '重写是子类重新定义父类方法，方法签名相同；重载是同一个类中方法名相同但参数列表不同', '重写发生在父子类之间，要求方法签名完全相同，访问权限不能缩小，异常不能扩大。重载发生在同一个类中，要求方法名相同但参数列表不同，与返回值无关', '重写,重载', 'Java基础', 1, 4),
(1, 5, '请简述HashMap的工作原理', 3, 5, 'HashMap基于数组+链表/红黑树实现，通过hash函数计算桶位置', 'HashMap底层是Node数组，通过key的hashCode计算数组下标。JDK8后当链表长度超过8时转为红黑树，容量小于64时优先扩容。默认负载因子0.75，扩容为2倍', 'HashMap,哈希表', 'Java集合', 1, 4),
(1, 5, '请解释Java的反射机制及其应用场景', 3, 5, '反射是在运行时获取类信息并操作类成员的机制，通过Class对象实现', '反射通过Class.forName/getClass等获取Class对象，可以动态创建实例、调用方法、访问字段。应用场景包括：框架设计(Spring IoC)、动态代理、注解处理、JDBC驱动加载等', '反射,Class', 'Java进阶', 1, 4),
(1, 5, '请简述JVM垃圾回收机制中可达性分析算法的原理', 4, 5, '从GC Roots出发，沿引用链遍历，不可达的对象即为垃圾', '可达性分析从GC Roots(包括栈中引用、静态变量、常量等)出发，遍历对象引用链。如果一个对象到GC Roots不可达，则判定为可回收对象', 'GC,可达性分析', 'JVM', 1, 4),
(1, 5, '请简述Java中volatile关键字的作用和原理', 4, 5, 'volatile保证变量的可见性和有序性，但不保证原子性', 'volatile通过内存屏障实现：1)保证线程修改后立即刷新到主内存；2)保证读取时从主内存读取最新值；3)禁止指令重排序。但volatile不保证原子性，如i++操作仍需synchronized或AtomicInteger', 'volatile,并发', 'Java并发', 1, 4);

SELECT 'Java题库扩展完成：单选10题 + 多选5题 + 判断5题 + 填空5题 + 简答5题 = 共30题' AS result;
