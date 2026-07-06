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

            // 检查并添加 file_url 字段
            ensureColumn(conn, "t_submission", "file_url",
                    "VARCHAR(500) COMMENT '上传文件的访问URL路径'",
                    "code_text");

            // 检查并添加 file_path 字段
            ensureColumn(conn, "t_submission", "file_path",
                    "VARCHAR(500) COMMENT '提交文件存储路径'",
                    "file_url");

            // 检查并添加 file_name 字段
            ensureColumn(conn, "t_submission", "file_name",
                    "VARCHAR(200) COMMENT '提交文件名'",
                    "file_path");

            log.info("========== 数据库结构检查完成 ==========");
        } catch (Exception e) {
            log.error("数据库结构检查失败: {}", e.getMessage(), e);
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
