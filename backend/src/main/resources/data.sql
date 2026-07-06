-- =============================================
-- Project One - 软件测试实践教学管理平台
-- 初始化测试数据 (数据库: teaching_platform)
-- 预置账号:
--   教师: teacher01 / 123456 (王老师)
--   学生: stu01 / 123456 (张三), stu02 / 123456 (李四), stu03 / 123456 (王五)
-- =============================================

-- ========== 初始化用户 ==========
-- 教师1个
INSERT IGNORE INTO t_user (id, username, password, real_name, role, class_name, student_no) VALUES
(1, 'teacher01', '123456', '王老师', 'TEACHER', NULL, NULL);

-- 学生3个
INSERT IGNORE INTO t_user (id, username, password, real_name, role, class_name, student_no) VALUES
(2, 'stu01', '123456', '张三', 'STUDENT', '软件工程2班', '2306002101');

INSERT IGNORE INTO t_user (id, username, password, real_name, role, class_name, student_no) VALUES
(3, 'stu02', '123456', '李四', 'STUDENT', '软件工程2班', '2306002102');

INSERT IGNORE INTO t_user (id, username, password, real_name, role, class_name, student_no) VALUES
(4, 'stu03', '123456', '王五', 'STUDENT', '软件工程1班', '2306001101');

-- ========== 初始化课程 ==========
INSERT IGNORE INTO t_course (id, course_name, description, teacher_id) VALUES
(1, '软件测试', '软件测试理论与方法，包括黑盒测试、白盒测试、自动化测试等内容', 1);

INSERT IGNORE INTO t_course (id, course_name, description, teacher_id) VALUES
(2, '软件工程', '软件开发流程、需求分析、系统设计、项目管理等', 1);

-- ========== 初始化实验任务 ==========
INSERT IGNORE INTO t_experiment_task (id, course_id, title, description, requirement, teacher_id, deadline, status) VALUES
(1, 1, '实验一：黑盒测试用例设计', '掌握等价类划分、边界值分析等黑盒测试方法，设计测试用例。', '1. 使用等价类划分法设计测试用例\n2. 使用边界值分析法设计测试用例\n3. 编写测试用例文档', 1, DATE_ADD(NOW(), INTERVAL 14 DAY), 1);

INSERT IGNORE INTO t_experiment_task (id, course_id, title, description, requirement, teacher_id, deadline, status) VALUES
(2, 1, '实验二：白盒测试与代码覆盖', '学习语句覆盖、分支覆盖、路径覆盖等白盒测试技术。', '1. 选择被测代码模块\n2. 绘制程序流程图\n3. 设计白盒测试用例\n4. 进行代码覆盖率分析', 1, DATE_ADD(NOW(), INTERVAL 21 DAY), 1);

INSERT IGNORE INTO t_experiment_task (id, course_id, title, description, requirement, teacher_id, deadline, status) VALUES
(3, 1, '实验三：自动化测试脚本开发', '使用Selenium等工具编写自动化测试脚本。', '1. 搭建自动化测试环境\n2. 编写登录功能自动化测试脚本\n3. 编写数据驱动测试脚本\n4. 生成测试报告', 1, DATE_ADD(NOW(), INTERVAL 30 DAY), 1);

-- ========== 初始化作业提交示例 ==========
-- 提交1: stu01仅提交代码文本，已批改
INSERT IGNORE INTO t_submission (id, task_id, student_id, code_text, status, score, comment, submit_time, review_time) VALUES
(1, 1, 2, '// 等价类划分测试用例（Java）\npublic class EquivalencePartitionTest {\n    // 有效等价类：1-100之间的整数\n    @Test\n    public void testValidInput() {\n        Calculator calc = new Calculator();\n        assertEquals(50, calc.compute(50));\n    }\n    // 无效等价类：小于1\n    @Test\n    public void testBelowRange() {\n        Calculator calc = new Calculator();\n        assertThrows(IllegalArgumentException.class, () -> calc.compute(-1));\n    }\n    // 无效等价类：大于100\n    @Test\n    public void testAboveRange() {\n        Calculator calc = new Calculator();\n        assertThrows(IllegalArgumentException.class, () -> calc.compute(101));\n    }\n}', 2, 88, '等价类划分正确，边界值测试需补充负数边界。代码格式规范，继续加油！', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 提交2: stu02仅提交文件（无代码文本），已批改
INSERT IGNORE INTO t_submission (id, task_id, student_id, file_path, file_url, file_name, status, score, comment, submit_time, review_time) VALUES
(2, 1, 3, '/uploads/submissions/1/3/sample_test_report.txt', '/uploads/submissions/1/3/sample_test_report.txt', '测试用例设计报告.txt', 2, 92, '测试用例设计非常全面，等价类和边界值覆盖完整，文档格式规范。', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 提交3: stu03文件+代码文本同时提交，待批改
INSERT IGNORE INTO t_submission (id, task_id, student_id, code_text, file_path, file_url, file_name, status, submit_time) VALUES
(3, 1, 4, '// 边界值分析法测试\n@Test\npublic void testBoundaryValues() {\n    // 上点：100\n    assertEquals(100, calc.compute(100));\n    // 离点：0, 101\n    assertThrows(Exception.class, () -> calc.compute(0));\n    assertThrows(Exception.class, () -> calc.compute(101));\n    // 内点：50\n    assertEquals(50, calc.compute(50));\n}', '/uploads/submissions/1/4/boundary_test_cases.xlsx', '/uploads/submissions/1/4/boundary_test_cases.xlsx', '边界值测试用例.xlsx', 1, DATE_SUB(NOW(), INTERVAL 2 DAY));
