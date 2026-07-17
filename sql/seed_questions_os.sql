-- ========== 操作系统 题库扩展脚本 ==========
-- 分类ID: 5 (操作系统)
-- 题型: 1单选 2多选 3判断 4填空 5简答
-- 难度: 1简单 2较易 3中等 4较难 5困难
-- creator_id: 4 (wang教师)

-- ==================== 一、单选题 (10题) ====================

-- S1: 进程与线程
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 1, '以下关于进程和线程的说法，正确的是？', 2, 2, 'B', '线程是CPU调度的基本单位，进程是资源分配的基本单位', '进程,线程', '操作系统', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '进程是CPU调度的基本单位', 0, 1), (LAST_INSERT_ID(), 'B', '线程是CPU调度的基本单位', 1, 2), (LAST_INSERT_ID(), 'C', '进程和线程都是CPU调度的基本单位', 0, 3), (LAST_INSERT_ID(), 'D', '线程是资源分配的基本单位', 0, 4);

-- S2: 进程状态
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 1, '进程从运行态变为阻塞态的原因通常是？', 2, 2, 'C', '进程因等待I/O或某个事件而主动阻塞自己', '进程状态,阻塞', '操作系统', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '时间片用完', 0, 1), (LAST_INSERT_ID(), 'B', '被高优先级进程抢占', 0, 2), (LAST_INSERT_ID(), 'C', '等待I/O操作完成', 1, 3), (LAST_INSERT_ID(), 'D', '进程创建完成', 0, 4);

-- S3: 页面置换算法
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 1, '以下哪种页面置换算法可能出现Belady异常？', 3, 2, 'A', 'FIFO算法在增加物理页框数时可能增加缺页率，即Belady异常', '页面置换,Belady', '内存管理', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', 'FIFO', 1, 1), (LAST_INSERT_ID(), 'B', 'LRU', 0, 2), (LAST_INSERT_ID(), 'C', 'OPT', 0, 3), (LAST_INSERT_ID(), 'D', 'LFU', 0, 4);

-- S4: 死锁条件
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 1, '以下哪个不是死锁产生的必要条件？', 3, 2, 'D', '死锁四个必要条件：互斥、占有并等待、不可抢占、循环等待。不可剥夺不是必要条件的名称', '死锁', '操作系统', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '互斥条件', 0, 1), (LAST_INSERT_ID(), 'B', '占有并等待', 0, 2), (LAST_INSERT_ID(), 'C', '循环等待', 0, 3), (LAST_INSERT_ID(), 'D', '同步条件', 1, 4);

-- S5: 信号量
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 1, '信号量S的值为-3，表示？', 3, 2, 'B', '信号量为负值时，其绝对值表示等待该信号量的进程数', '信号量,同步', '进程同步', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '有3个进程可以进入临界区', 0, 1), (LAST_INSERT_ID(), 'B', '有3个进程在等待队列中', 1, 2), (LAST_INSERT_ID(), 'C', '有3个资源可用', 0, 3), (LAST_INSERT_ID(), 'D', '发生了3次死锁', 0, 4);

-- S6: 调度算法
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 1, '以下哪种调度算法可能导致饥饿现象？', 3, 2, 'C', '优先级调度算法中，低优先级进程可能长期得不到执行导致饥饿', '调度,饥饿', '处理机调度', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '先来先服务(FCFS)', 0, 1), (LAST_INSERT_ID(), 'B', '时间片轮转(RR)', 0, 2), (LAST_INSERT_ID(), 'C', '优先级调度', 1, 3), (LAST_INSERT_ID(), 'D', '短作业优先(SJF)', 0, 4);

-- S7: 虚拟内存
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 1, '虚拟内存的实现基础是？', 2, 2, 'A', '虚拟内存基于局部性原理，包括时间局部性和空间局部性', '虚拟内存,局部性', '内存管理', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '局部性原理', 1, 1), (LAST_INSERT_ID(), 'B', '互斥原理', 0, 2), (LAST_INSERT_ID(), 'C', '中断原理', 0, 3), (LAST_INSERT_ID(), 'D', '分时原理', 0, 4);

-- S8: 文件系统
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 1, 'UNIX文件系统采用的磁盘块分配方式是？', 3, 2, 'B', 'UNIX文件系统(i-node)采用混合索引分配方式', '文件系统,磁盘分配', '文件管理', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '连续分配', 0, 1), (LAST_INSERT_ID(), 'B', '索引分配', 1, 2), (LAST_INSERT_ID(), 'C', '链接分配', 0, 3), (LAST_INSERT_ID(), 'D', '动态分配', 0, 4);

-- S9: 中断
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 1, '以下哪种中断属于外中断(中断)而非内中断(异常)？', 3, 2, 'A', '时钟中断是由CPU外部的时钟设备产生的，属于外中断', '中断', '操作系统', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '时钟中断', 1, 1), (LAST_INSERT_ID(), 'B', '缺页异常', 0, 2), (LAST_INSERT_ID(), 'C', '除零异常', 0, 3), (LAST_INSERT_ID(), 'D', '溢出异常', 0, 4);

-- S10: 磁盘调度
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 1, 'SCAN算法(电梯算法)的特点是？', 2, 2, 'B', 'SCAN算法磁头单方向移动到尽头再反向，类似电梯运行', '磁盘调度,SCAN', 'IO管理', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '先来先服务', 0, 1), (LAST_INSERT_ID(), 'B', '磁头单向移动到尽头再反向', 1, 2), (LAST_INSERT_ID(), 'C', '总是寻找最近的磁道', 0, 3), (LAST_INSERT_ID(), 'D', '随机选择磁道', 0, 4);

-- ==================== 二、多选题 (5题) ====================

-- M1: 进程通信
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 2, '以下哪些是进程间通信(IPC)的方式？', 2, 4, 'A,B,C', '管道、共享内存、消息队列都是IPC方式，局部变量不是', '进程通信,IPC', '操作系统', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '管道(Pipe)', 1, 1), (LAST_INSERT_ID(), 'B', '共享内存', 1, 2), (LAST_INSERT_ID(), 'C', '消息队列', 1, 3), (LAST_INSERT_ID(), 'D', '局部变量', 0, 4);

-- M2: 死锁处理
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 2, '处理死锁的方法包括以下哪些？', 2, 4, 'A,B,C', '死锁预防、死锁避免、死锁检测与恢复都是处理方法，并行处理不是', '死锁', '操作系统', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '死锁预防', 1, 1), (LAST_INSERT_ID(), 'B', '死锁避免', 1, 2), (LAST_INSERT_ID(), 'C', '死锁检测与恢复', 1, 3), (LAST_INSERT_ID(), 'D', '并行处理', 0, 4);

-- M3: 内存分配
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 2, '以下哪些属于动态分区分配算法？', 3, 4, 'A,B,D', '首次适应、最佳适应、最坏适应都是动态分区算法，固定分区不是', '内存分配', '内存管理', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '首次适应算法', 1, 1), (LAST_INSERT_ID(), 'B', '最佳适应算法', 1, 2), (LAST_INSERT_ID(), 'C', '固定分区法', 0, 3), (LAST_INSERT_ID(), 'D', '最坏适应算法', 1, 4);

-- M4: 线程同步
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 2, '以下哪些是线程同步的机制？', 3, 4, 'A,B,C', '互斥锁、信号量、条件变量都是同步机制，轮询不是一种同步机制', '线程同步', '进程同步', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '互斥锁(Mutex)', 1, 1), (LAST_INSERT_ID(), 'B', '信号量(Semaphore)', 1, 2), (LAST_INSERT_ID(), 'C', '条件变量', 1, 3), (LAST_INSERT_ID(), 'D', '轮询(Polling)', 0, 4);

-- M5: OS功能
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 2, '操作系统的主要功能包括以下哪些？', 1, 4, 'A,B,C', '处理机管理、存储器管理、设备管理都是OS核心功能，编译程序不属于OS功能', 'OS功能', '操作系统', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '处理机管理', 1, 1), (LAST_INSERT_ID(), 'B', '存储器管理', 1, 2), (LAST_INSERT_ID(), 'C', '设备管理', 1, 3), (LAST_INSERT_ID(), 'D', '编译程序', 0, 4);

-- ==================== 三、判断题 (5题) ====================

-- T1
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 3, '进程和程序是一一对应的', 1, 2, 'F', '一个程序可以对应多个进程，一个进程也可以执行多个程序', '进程,程序', '操作系统', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 0, 1), (LAST_INSERT_ID(), 'B', '错误', 1, 2);

-- T2
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 3, '银行家算法是一种死锁检测算法', 2, 2, 'F', '银行家算法是死锁避免算法，不是检测算法', '银行家算法,死锁', '操作系统', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 0, 1), (LAST_INSERT_ID(), 'B', '错误', 1, 2);

-- T3
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 3, '分页存储管理会产生外部碎片', 2, 2, 'F', '分页存储产生内部碎片，分段存储产生外部碎片', '分页,碎片', '内存管理', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 0, 1), (LAST_INSERT_ID(), 'B', '错误', 1, 2);

-- T4
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 3, '临界区是指访问共享资源的代码段', 1, 2, 'T', '临界区是进程中访问临界资源的代码段，需要互斥进入', '临界区,互斥', '进程同步', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 1, 1), (LAST_INSERT_ID(), 'B', '错误', 0, 2);

-- T5
INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id)
VALUES (5, 3, '段页式存储结合了分页和分段的优点', 2, 2, 'T', '段页式先分段再分页，既有分段的逻辑清晰性，又有分页的内存利用率', '段页式,内存管理', '内存管理', 1, 4);
INSERT INTO question_option (question_id, option_label, option_content, is_correct, sort_no) VALUES
(LAST_INSERT_ID(), 'A', '正确', 1, 1), (LAST_INSERT_ID(), 'B', '错误', 0, 2);

-- ==================== 四、填空题 (5题，无选项) ====================

INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id) VALUES
(5, 4, '进程的三种基本状态是就绪态、运行态和____态', 1, 2, '阻塞', '进程的三种基本状态：就绪(Ready)、运行(Running)、阻塞(Blocked/Waiting)', '进程状态', '操作系统', 1, 4),
(5, 4, '产生死锁的四个必要条件是互斥、占有并等待、____和循环等待', 2, 2, '不可抢占', '死锁四个必要条件：互斥、占有并等待、不可抢占、循环等待', '死锁', '操作系统', 1, 4),
(5, 4, '页面置换算法中，____算法是理论最优但无法实现的', 3, 2, 'OPT', 'OPT(最佳置换算法)淘汰最长时间不会被访问的页面，但需要预知未来，无法实现', '页面置换,OPT', '内存管理', 1, 4),
(5, 4, '在分页存储管理中，逻辑地址由____和页内偏移组成', 2, 2, '页号', '逻辑地址 = 页号 + 页内偏移(页内地址)', '分页,地址', '内存管理', 1, 4),
(5, 4, '磁盘访问时间由寻道时间、____时间和传输时间三部分组成', 2, 2, '旋转延迟', '磁盘访问时间 = 寻道时间 + 旋转延迟 + 传输时间', '磁盘,访问时间', 'IO管理', 1, 4);

-- ==================== 五、简答题 (5题，无选项) ====================

INSERT INTO question (category_id, question_type, title, difficulty, score, correct_answer, answer_analysis, knowledge_points, tags, status, creator_id) VALUES
(5, 5, '请简述进程和线程的区别', 2, 5, '进程是资源分配的基本单位，线程是CPU调度的基本单位；一个进程可包含多个线程', '进程是资源分配的基本单位，拥有独立的地址空间；线程是CPU调度的基本单位，共享进程的地址空间和资源。线程切换开销小于进程切换。进程间通信需要IPC机制，线程间可直接通信', '进程,线程', '操作系统', 1, 4),
(5, 5, '请简述什么是死锁，以及死锁的四个必要条件', 2, 5, '死锁是多个进程互相等待对方持有的资源而无法继续执行的状态', '死锁是两个或多个进程在执行过程中因互相等待对方持有的资源而造成的一种僵局。四个必要条件：1)互斥条件；2)占有并等待条件；3)不可抢占条件；4)循环等待条件', '死锁', '操作系统', 1, 4),
(5, 5, '请简述分页和分段存储管理的区别', 3, 5, '分页是物理划分，页大小固定；分段是逻辑划分，段大小可变', '分页：以物理单位划分，页大小固定，对程序员透明，消除了外部碎片。分段：以逻辑单位划分，段大小可变，反映程序逻辑结构，方便共享和保护。分段会产生外部碎片', '分页,分段', '内存管理', 1, 4),
(5, 5, '请解释什么是局部性原理，并说明其在虚拟内存中的作用', 3, 5, '局部性原理包括时间局部性和空间局部性，是虚拟内存高效运行的理论基础', '局部性原理：1)时间局部性——最近访问的数据很可能再次被访问；2)空间局部性——访问某个地址后，附近的地址也很可能被访问。虚拟内存利用这一原理，只需将活跃页面放在内存中，即可获得接近全部在内存的性能', '局部性,虚拟内存', '内存管理', 1, 4),
(5, 5, '请简述生产者-消费者问题及其解决方案', 3, 5, '生产者-消费者是经典的进程同步问题，需要使用信号量实现互斥和同步', '生产者-消费者问题：生产者向缓冲区放数据，消费者从缓冲区取数据。需要三个信号量：mutex(互斥访问缓冲区)、empty(空缓冲区数)、full(满缓冲区数)。生产者P(empty)->P(mutex)->放入->V(mutex)->V(full)；消费者P(full)->P(mutex)->取出->V(mutex)->V(empty)', '生产者消费者,信号量', '进程同步', 1, 4);

SELECT '操作系统题库扩展完成：单选10题 + 多选5题 + 判断5题 + 填空5题 + 简答5题 = 共30题' AS result;
