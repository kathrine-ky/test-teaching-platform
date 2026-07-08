package com.software.testing.submitmodule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.software.testing.submitmodule.entity.Submission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {

    /** 查询任务所有学生提交情况（包括未提交的学生），先通过任务找到课程，再找课程下所有学生 */
    @Select("SELECT s.id, s.task_id as taskId, s.student_id as studentId, " +
            "s.test_case as testCase, s.defect_report as defectReport, s.test_summary as testSummary, " +
            "s.code_text as codeText, " +
            "s.file_url as fileUrl, s.file_path as filePath, s.file_name as fileName, " +
            "s.file_url2 as fileUrl2, s.file_path2 as filePath2, s.file_name2 as fileName2, " +
            "s.file_url3 as fileUrl3, s.file_path3 as filePath3, s.file_name3 as fileName3, " +
            "s.status, s.score, s.comment, s.submit_time as submitTime, s.review_time as reviewTime, " +
            "u.real_name as studentName, u.student_no as studentNo, u.class_name as className " +
            "FROM t_experiment_task et " +
            "JOIN t_course_student cs ON et.course_id = cs.course_id " +
            "JOIN t_user u ON cs.student_id = u.id " +
            "LEFT JOIN t_submission s ON s.task_id = et.id AND s.student_id = cs.student_id " +
            "WHERE et.id = #{taskId} " +
            "ORDER BY u.student_no ASC")
    List<Map<String, Object>> selectWithStudentByTaskId(Long taskId);

    @Select("SELECT s.score, s.id as submissionId, s.task_id as taskId, " +
            "u.real_name as studentName, u.student_no as studentNo, u.class_name as className " +
            "FROM t_submission s LEFT JOIN t_user u ON s.student_id = u.id " +
            "WHERE s.task_id = #{taskId} AND s.status = 2")
    List<Map<String, Object>> selectScoresByTaskId(Long taskId);

    @Select("SELECT s.id, s.task_id as taskId, s.student_id as studentId, " +
            "s.test_case as testCase, s.defect_report as defectReport, s.test_summary as testSummary, " +
            "s.code_text as codeText, " +
            "s.file_url as fileUrl, s.file_path as filePath, s.file_name as fileName, " +
            "s.file_url2 as fileUrl2, s.file_path2 as filePath2, s.file_name2 as fileName2, " +
            "s.file_url3 as fileUrl3, s.file_path3 as filePath3, s.file_name3 as fileName3, " +
            "s.status, s.score, s.comment, s.submit_time as submitTime, s.review_time as reviewTime, " +
            "et.title as taskTitle " +
            "FROM t_submission s LEFT JOIN t_experiment_task et ON s.task_id = et.id " +
            "WHERE s.student_id = #{studentId} " +
            "ORDER BY s.submit_time DESC")
    List<Map<String, Object>> selectByStudentId(Long studentId);

    /** 统计任务提交状态分布：status=0未提交, status=1已提交待批改, status=2已批改 */
    @Select("SELECT COALESCE(SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END), 0) as notSubmitted, " +
            "COALESCE(SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END), 0) as submitted, " +
            "COALESCE(SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END), 0) as reviewed, " +
            "COUNT(*) as total " +
            "FROM t_submission WHERE task_id = #{taskId}")
    Map<String, Object> selectStatusStatsByTaskId(Long taskId);

    /** 教师端：获取教师所有已发布任务的提交状态汇总（每个任务的已提交/未提交人数） */
    @Select("SELECT et.id as taskId, et.title as taskTitle, et.deadline, et.course_id as courseId, " +
            "c.course_name as courseName, " +
            "COALESCE(SUM(CASE WHEN s.status IS NOT NULL AND s.status IN (1,2) THEN 1 ELSE 0 END), 0) as submittedCount, " +
            "COALESCE(SUM(CASE WHEN s.status = 2 THEN 1 ELSE 0 END), 0) as reviewedCount, " +
            "COALESCE(COUNT(DISTINCT cs.student_id), 0) as totalStudents, " +
            "COALESCE(COUNT(DISTINCT cs.student_id) - SUM(CASE WHEN s.status IS NOT NULL AND s.status IN (1,2) THEN 1 ELSE 0 END), 0) as notSubmittedCount " +
            "FROM t_experiment_task et " +
            "LEFT JOIN t_course c ON et.course_id = c.id " +
            "LEFT JOIN t_course_student cs ON et.course_id = cs.course_id " +
            "LEFT JOIN t_submission s ON s.task_id = et.id AND s.student_id = cs.student_id " +
            "WHERE et.teacher_id = #{teacherId} AND et.status = 1 " +
            "GROUP BY et.id, et.title, et.deadline, et.course_id, c.course_name " +
            "ORDER BY et.create_time DESC")
    List<Map<String, Object>> selectTaskSubmissionSummaryByTeacherId(Long teacherId);
}
