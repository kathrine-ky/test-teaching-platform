package com.software.testing.submitmodule.service;

import com.software.testing.common.Result;
import com.software.testing.scoremodule.dto.ReviewDTO;
import org.springframework.web.multipart.MultipartFile;

public interface SubmissionService {
    Result<?> submit(Long taskId, Long studentId, String testCase, String defectReport, String testSummary, String codeText,
                     MultipartFile file, MultipartFile file2, MultipartFile file3);
    Result<?> getMySubmission(Long taskId, Long studentId);
    Result<?> getSubmissionsByTask(Long taskId);
    Result<?> review(ReviewDTO reviewDTO);
    Result<?> getScoreStatistics(Long taskId);
    byte[] exportScores(Long taskId);
    Result<?> getMyScores(Long studentId);

    /** 教师端：获取所有任务的提交状态汇总 */
    Result<?> getTeacherSubmissionSummary(Long teacherId);
}
