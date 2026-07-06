-- =============================================
-- Project One - 软件测试实践教学管理平台
-- MySQL建表语句 (数据库: teaching_platform)
-- =============================================

CREATE DATABASE IF NOT EXISTS teaching_platform DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE teaching_platform;

-- 用户表 (教师+学生)
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色: TEACHER/STUDENT',
    class_name VARCHAR(100) COMMENT '班级',
    student_no VARCHAR(50) COMMENT '学号',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 课程表
CREATE TABLE IF NOT EXISTS t_course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(200) NOT NULL COMMENT '课程名称',
    description TEXT COMMENT '课程描述',
    teacher_id BIGINT NOT NULL COMMENT '授课教师ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 实验任务/作业表
CREATE TABLE IF NOT EXISTS t_experiment_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT COMMENT '关联课程ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    requirement TEXT COMMENT '实验要求',
    teacher_id BIGINT NOT NULL COMMENT '发布教师ID',
    deadline DATETIME COMMENT '截止时间',
    status INT DEFAULT 0 COMMENT '状态: 0-草稿 1-已发布 2-已截止',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验任务表';

-- 作业提交表
CREATE TABLE IF NOT EXISTS t_submission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL COMMENT '任务ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    code_text TEXT COMMENT '学生提交的代码文本',
    file_url VARCHAR(500) COMMENT '上传文件的访问URL路径',
    file_path VARCHAR(500) COMMENT '提交文件存储路径',
    file_name VARCHAR(200) COMMENT '提交文件名',
    status INT DEFAULT 0 COMMENT '状态: 0-未提交 1-已提交 2-已批改',
    score INT COMMENT '评分 0-100',
    comment TEXT COMMENT '教师评语',
    submit_time DATETIME COMMENT '提交时间',
    review_time DATETIME COMMENT '批改时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- =============================================
-- 兼容性补丁：为已存在的旧表补充缺失字段
-- 如果字段已存在，ALTER TABLE ADD COLUMN 会报错但不影响后续执行
-- 使用存储过程实现安全添加（忽略字段已存在的错误）
-- =============================================

-- 为 t_submission 表安全添加 code_text 字段（如果不存在）
SET @sql_code_text = (SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE t_submission ADD COLUMN code_text TEXT COMMENT ''学生提交的代码文本'' AFTER student_id',
    'SELECT ''code_text already exists'''
) FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'teaching_platform' AND TABLE_NAME = 't_submission' AND COLUMN_NAME = 'code_text');
PREPARE stmt FROM @sql_code_text; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 为 t_submission 表安全添加 file_url 字段（如果不存在）
SET @sql_file_url = (SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE t_submission ADD COLUMN file_url VARCHAR(500) COMMENT ''上传文件的访问URL路径'' AFTER code_text',
    'SELECT ''file_url already exists'''
) FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'teaching_platform' AND TABLE_NAME = 't_submission' AND COLUMN_NAME = 'file_url');
PREPARE stmt FROM @sql_file_url; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 为 t_submission 表安全添加 file_path 字段（如果不存在）
SET @sql_file_path = (SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE t_submission ADD COLUMN file_path VARCHAR(500) COMMENT ''提交文件存储路径'' AFTER file_url',
    'SELECT ''file_path already exists'''
) FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'teaching_platform' AND TABLE_NAME = 't_submission' AND COLUMN_NAME = 'file_path');
PREPARE stmt FROM @sql_file_path; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 为 t_submission 表安全添加 file_name 字段（如果不存在）
SET @sql_file_name = (SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE t_submission ADD COLUMN file_name VARCHAR(200) COMMENT ''提交文件名'' AFTER file_path',
    'SELECT ''file_name already exists'''
) FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'teaching_platform' AND TABLE_NAME = 't_submission' AND COLUMN_NAME = 'file_name');
PREPARE stmt FROM @sql_file_name; EXECUTE stmt; DEALLOCATE PREPARE stmt;
