package com.software.testing.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 应用启动时自动检查并修复数据库表结构
 * 确保 t_submission 表包含 code_text, file_url, file_path, file_name 字段
 * 确保 t_user 表包含 gender 字段
 * 确保 t_course_student 表存在
 * 确保 admin 账号和初始课程数据存在
 */
@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        try (Connection conn = dataSource.getConnection()) {
            log.info("========== 数据库结构检查开始 ==========");

            // 检查并添加 code_text 字段
            ensureColumn(conn, "t_submission", "code_text",
                    "TEXT COMMENT '学生提交的代码文本'",
                    "student_id");

            // 检查并添加 test_case 字段
            ensureColumn(conn, "t_submission", "test_case",
                    "TEXT COMMENT '测试用例文本'",
                    "student_id");

            // 检查并添加 defect_report 字段
            ensureColumn(conn, "t_submission", "defect_report",
                    "TEXT COMMENT '缺陷报告文本'",
                    "test_case");

            // 检查并添加 test_summary 字段
            ensureColumn(conn, "t_submission", "test_summary",
                    "TEXT COMMENT '测试总结文本'",
                    "defect_report");

            // 检查并添加 file_url 字段
            ensureColumn(conn, "t_submission", "file_url",
                    "VARCHAR(500) COMMENT '上传文件的访问URL路径'",
                    "test_summary");

            // 检查并添加 file_path 字段
            ensureColumn(conn, "t_submission", "file_path",
                    "VARCHAR(500) COMMENT '提交文件存储路径'",
                    "file_url");

            // 检查并添加 file_name 字段
            ensureColumn(conn, "t_submission", "file_name",
                    "VARCHAR(200) COMMENT '提交文件名'",
                    "file_path");

            // 检查并添加第二个文件字段
            ensureColumn(conn, "t_submission", "file_url2",
                    "VARCHAR(500) COMMENT '第二个文件的访问URL路径'",
                    "file_name");
            ensureColumn(conn, "t_submission", "file_path2",
                    "VARCHAR(500) COMMENT '第二个文件存储路径'",
                    "file_url2");
            ensureColumn(conn, "t_submission", "file_name2",
                    "VARCHAR(200) COMMENT '第二个文件名'",
                    "file_path2");

            // 检查并添加第三个文件字段
            ensureColumn(conn, "t_submission", "file_url3",
                    "VARCHAR(500) COMMENT '第三个文件的访问URL路径'",
                    "file_name2");
            ensureColumn(conn, "t_submission", "file_path3",
                    "VARCHAR(500) COMMENT '第三个文件存储路径'",
                    "file_url3");
            ensureColumn(conn, "t_submission", "file_name3",
                    "VARCHAR(200) COMMENT '第三个文件名'",
                    "file_path3");

            // 检查并添加 t_user 的 gender 字段
            ensureColumn(conn, "t_user", "gender",
                    "VARCHAR(10) COMMENT '性别: 男/女'",
                    "email");

            // 确保 admin 管理员账号存在
            ensureAdminUser(conn);

            // 确保 t_course_student 表存在
            ensureCourseStudentTable(conn);

            // 确保初始课程数据存在
            ensureInitCourses(conn);

            // 确保初始学生课程分配
            ensureInitCourseAssign(conn);

            // 确保 t_task_file 表存在
            ensureTaskFileTable(conn);

            // 确保讨论区表存在
            ensureDiscussionTables(conn);

            // 确保课程资源表存在
            ensureCourseFileTable(conn);

            log.info("========== 数据库结构检查完成 ==========");
        } catch (Exception e) {
            log.error("数据库结构检查失败: {}", e.getMessage(), e);
        }
    }

    private void ensureAdminUser(Connection conn) {
        try {
            // 检查 admin 用户是否已存在
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) FROM t_user WHERE username = 'admin'")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    log.info("[检查通过] admin 管理员账号已存在");
                    return;
                }
            }
            // 不存在则插入
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                    "INSERT INTO t_user (username, password, real_name, role, gender) " +
                    "VALUES ('admin', '123456', '系统管理员', 'ADMIN', '男')");
                log.info("[初始化成功] 已创建 admin 管理员账号");
            }
        } catch (Exception e) {
            log.warn("[初始化跳过] 创建 admin 管理员账号失败: {}", e.getMessage());
        }
    }

    private void ensureCourseStudentTable(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "t_course_student", null)) {
                if (rs.next()) {
                    log.info("[检查通过] t_course_student 表已存在");
                    return;
                }
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                    "CREATE TABLE t_course_student (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "course_id BIGINT NOT NULL COMMENT '课程ID', " +
                    "student_id BIGINT NOT NULL COMMENT '学生ID', " +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间', " +
                    "UNIQUE KEY uk_course_student (course_id, student_id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程学生关联表'");
                log.info("[初始化成功] 已创建 t_course_student 表");
            }
        } catch (Exception e) {
            log.warn("[初始化跳过] 创建 t_course_student 表失败: {}", e.getMessage());
        }
    }

    private void ensureInitCourses(Connection conn) {
        try {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_course")) {
                if (rs.next() && rs.getInt(1) >= 6) {
                    log.info("[检查通过] 初始课程数据已存在 ({}门)", rs.getInt(1));
                    return;
                }
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                    "INSERT IGNORE INTO t_course (id, course_name, description, teacher_id) VALUES " +
                    "(1, '数据结构与算法', '常见数据结构（数组、链表、栈、队列、树、图）与经典算法（排序、查找、动态规划等）', 1)");
                stmt.executeUpdate(
                    "INSERT IGNORE INTO t_course (id, course_name, description, teacher_id) VALUES " +
                    "(2, '软件测试基础', '软件测试基本概念、测试流程、测试策略、测试文档编写等基础知识', 1)");
                stmt.executeUpdate(
                    "INSERT IGNORE INTO t_course (id, course_name, description, teacher_id) VALUES " +
                    "(3, '黑盒测试技术', '等价类划分、边界值分析、因果图、决策表、正交实验法等黑盒测试方法', 1)");
                stmt.executeUpdate(
                    "INSERT IGNORE INTO t_course (id, course_name, description, teacher_id) VALUES " +
                    "(4, '白盒测试技术', '语句覆盖、分支覆盖、路径覆盖、条件覆盖等白盒测试技术与代码审查', 1)");
                stmt.executeUpdate(
                    "INSERT IGNORE INTO t_course (id, course_name, description, teacher_id) VALUES " +
                    "(5, '自动化测试', 'Selenium、JUnit、TestNG等自动化测试框架的使用与脚本开发', 1)");
                stmt.executeUpdate(
                    "INSERT IGNORE INTO t_course (id, course_name, description, teacher_id) VALUES " +
                    "(6, '软件缺陷管理', '缺陷生命周期、缺陷报告编写、缺陷跟踪工具（Jira/Bugzilla）使用', 1)");
                log.info("[初始化成功] 已创建6门初始课程");
            }
        } catch (Exception e) {
            log.warn("[初始化跳过] 创建初始课程失败: {}", e.getMessage());
        }
    }

    private void ensureInitCourseAssign(Connection conn) {
        try {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_course_student")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    log.info("[检查通过] 课程学生分配数据已存在");
                    return;
                }
            }
            try (Statement stmt = conn.createStatement()) {
                // 给3个学生各分配几门课程
                stmt.executeUpdate("INSERT IGNORE INTO t_course_student (course_id, student_id) VALUES " +
                    "(1,2),(1,3),(2,2),(2,3),(2,4),(3,2),(3,4),(4,3),(4,4),(5,2),(5,3),(6,4)");
                log.info("[初始化成功] 已创建课程学生分配数据");
            }
        } catch (Exception e) {
            log.warn("[初始化跳过] 创建课程分配数据失败: {}", e.getMessage());
        }
    }

    private void ensureTaskFileTable(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "t_task_file", null)) {
                if (rs.next()) {
                    log.info("[检查通过] t_task_file 表已存在");
                    return;
                }
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                    "CREATE TABLE t_task_file (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "task_id BIGINT NOT NULL COMMENT '关联任务ID', " +
                    "file_name VARCHAR(255) NOT NULL COMMENT '原始文件名', " +
                    "file_path VARCHAR(500) NOT NULL COMMENT '存储路径', " +
                    "file_url VARCHAR(500) NOT NULL COMMENT '访问URL', " +
                    "file_size BIGINT DEFAULT 0 COMMENT '文件大小(字节)', " +
                    "file_type VARCHAR(50) COMMENT '文件类型', " +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间'" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验任务附件表'");
                log.info("[初始化成功] 已创建 t_task_file 表");
            }
        } catch (Exception e) {
            log.warn("[初始化跳过] 创建 t_task_file 表失败: {}", e.getMessage());
        }
    }

    private void ensureDiscussionTables(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            // 确保 t_discussion_post 表存在
            try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "t_discussion_post", null)) {
                if (rs.next()) {
                    log.info("[检查通过] t_discussion_post 表已存在");
                } else {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate(
                            "CREATE TABLE t_discussion_post (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "user_id BIGINT NOT NULL COMMENT '发帖人ID', " +
                            "title VARCHAR(200) NOT NULL COMMENT '帖子标题', " +
                            "content TEXT NOT NULL COMMENT '帖子内容', " +
                            "view_count INT DEFAULT 0 COMMENT '浏览量', " +
                            "like_count INT DEFAULT 0 COMMENT '点赞数', " +
                            "comment_count INT DEFAULT 0 COMMENT '评论数', " +
                            "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发帖时间', " +
                            "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论区帖子表'");
                        log.info("[初始化成功] 已创建 t_discussion_post 表");
                    }
                }
            }
            // 确保 t_discussion_comment 表存在
            try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "t_discussion_comment", null)) {
                if (rs.next()) {
                    log.info("[检查通过] t_discussion_comment 表已存在");
                } else {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate(
                            "CREATE TABLE t_discussion_comment (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "post_id BIGINT NOT NULL COMMENT '帖子ID', " +
                            "user_id BIGINT NOT NULL COMMENT '评论人ID', " +
                            "content TEXT NOT NULL COMMENT '评论内容', " +
                            "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间'" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论区评论表'");
                        log.info("[初始化成功] 已创建 t_discussion_comment 表");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[初始化跳过] 创建讨论区表失败: {}", e.getMessage());
        }
    }

    private void ensureCourseFileTable(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "t_course_file", null)) {
                if (rs.next()) {
                    log.info("[检查通过] t_course_file 表已存在");
                    return;
                }
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                    "CREATE TABLE t_course_file (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "course_id BIGINT NOT NULL COMMENT '课程ID', " +
                    "file_name VARCHAR(255) NOT NULL COMMENT '原始文件名', " +
                    "file_path VARCHAR(500) NOT NULL COMMENT '存储路径', " +
                    "file_url VARCHAR(500) NOT NULL COMMENT '访问URL', " +
                    "file_size BIGINT DEFAULT 0 COMMENT '文件大小(字节)', " +
                    "file_type VARCHAR(50) COMMENT '文件类型', " +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间'" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源文件表'");
                log.info("[初始化成功] 已创建 t_course_file 表");
            }
        } catch (Exception e) {
            log.warn("[初始化跳过] 创建 t_course_file 表失败: {}", e.getMessage());
        }
    }

    private void ensureColumn(Connection conn, String tableName, String columnName,
                              String columnDef, String afterColumn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            // MySQL 表名默认小写，DatabaseMetaData 会做大小写不敏感匹配
            try (ResultSet rs = meta.getColumns(
                    conn.getCatalog(), null, tableName, columnName)) {
                if (rs.next()) {
                    log.info("[检查通过] 表 {} 字段 {} 已存在", tableName, columnName);
                    return;
                }
            }

            // 字段不存在，执行 ALTER TABLE
            String sql = String.format("ALTER TABLE %s ADD COLUMN %s %s AFTER %s",
                    tableName, columnName, columnDef, afterColumn);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
                log.info("[修复成功] 表 {} 已添加字段 {} -> {}", tableName, columnName, columnDef);
            }
        } catch (Exception e) {
            log.warn("[修复跳过] 表 {} 添加字段 {} 失败: {}", tableName, columnName, e.getMessage());
        }
    }
}
