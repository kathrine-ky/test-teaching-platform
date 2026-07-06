package com.software.testing.submitmodule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.software.testing.submitmodule.entity.Submission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {

    @Select("SELECT s.id, s.task_id as taskId, s.student_id as studentId, " +
            "s.code_text as codeText, s.file_url as fileUrl, s.file_path as filePath, s.file_name as fileName, " +
            "s.status, s.score, s.comment, s.submit_time as submitTime, s.review_time as reviewTime, " +
            "u.real_name as studentName, u.student_no as studentNo, u.class_name as className " +
            "FROM t_submission s LEFT JOIN t_user u ON s.student_id = u.id " +
            "WHERE s.task_id = #{taskId} " +
            "ORDER BY s.submit_time DESC")
    List<Map<String, Object>> selectWithStudentByTaskId(Long taskId);

    @Select("SELECT s.score, s.id as submissionId, s.task_id as taskId, " +
            "u.real_name as studentName, u.student_no as studentNo, u.class_name as className " +
            "FROM t_submission s LEFT JOIN t_user u ON s.student_id = u.id " +
            "WHERE s.task_id = #{taskId} AND s.status = 2")
    List<Map<String, Object>> selectScoresByTaskId(Long taskId);

    @Select("SELECT s.id, s.task_id as taskId, s.student_id as studentId, " +
            "s.code_text as codeText, s.file_url as fileUrl, s.file_path as filePath, s.file_name as fileName, " +
            "s.status, s.score, s.comment, s.submit_time as submitTime, s.review_time as reviewTime, " +
            "et.title as taskTitle " +
            "FROM t_submission s LEFT JOIN t_experiment_task et ON s.task_id = et.id " +
            "WHERE s.student_id = #{studentId} " +
            "ORDER BY s.submit_time DESC")
    List<Map<String, Object>> selectByStudentId(Long studentId);
}
