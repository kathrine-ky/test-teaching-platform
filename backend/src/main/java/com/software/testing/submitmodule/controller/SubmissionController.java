package com.software.testing.submitmodule.controller;

import com.software.testing.common.Result;
import com.software.testing.scoremodule.dto.ReviewDTO;
import com.software.testing.submitmodule.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/submission")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/submit/{taskId}")
    public Result<?> submit(@PathVariable Long taskId,
                            @RequestParam(required = false) String testCase,
                            @RequestParam(required = false) String defectReport,
                            @RequestParam(required = false) String testSummary,
                            @RequestParam(required = false) String codeText,
                            @RequestParam(required = false) MultipartFile file,
                            @RequestParam(required = false) MultipartFile file2,
                            @RequestParam(required = false) MultipartFile file3,
                            HttpServletRequest request) {
        Long studentId = (Long) request.getAttribute("userId");
        return submissionService.submit(taskId, studentId, testCase, defectReport, testSummary, codeText, file, file2, file3);
    }

    @GetMapping("/my/{taskId}")
    public Result<?> getMySubmission(@PathVariable Long taskId, HttpServletRequest request) {
        Long studentId = (Long) request.getAttribute("userId");
        return submissionService.getMySubmission(taskId, studentId);
    }

    @GetMapping("/myScores")
    public Result<?> getMyScores(HttpServletRequest request) {
        Long studentId = (Long) request.getAttribute("userId");
        return submissionService.getMyScores(studentId);
    }

    @GetMapping("/list/{taskId}")
    public Result<?> getSubmissionsByTask(@PathVariable Long taskId) {
        return submissionService.getSubmissionsByTask(taskId);
    }

    @PostMapping("/review")
    public Result<?> review(@Valid @RequestBody ReviewDTO reviewDTO) {
        return submissionService.review(reviewDTO);
    }

    @GetMapping("/statistics/{taskId}")
    public Result<?> statistics(@PathVariable Long taskId) {
        return submissionService.getScoreStatistics(taskId);
    }

    @GetMapping("/export/{taskId}")
    public ResponseEntity<byte[]> export(@PathVariable Long taskId) {
        byte[] data = submissionService.exportScores(taskId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scores.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    /** 教师端：获取所有已发布任务的提交状态汇总 */
    @GetMapping("/teacher/submission-summary")
    public Result<?> getTeacherSubmissionSummary(HttpServletRequest request) {
        Long teacherId = (Long) request.getAttribute("userId");
        return submissionService.getTeacherSubmissionSummary(teacherId);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String path) {
        try {
            // 去掉可能的前导斜杠，确保路径拼接正确
            String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
            File file = new File("./" + normalizedPath);
            if (!file.exists()) {
                // 尝试直接用绝对路径查找（uploadDir可能是相对路径）
                file = new File(normalizedPath);
                if (!file.exists()) {
                    return ResponseEntity.notFound().build();
                }
            }
            Resource resource = new FileSystemResource(file);
            String encodedName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename*=UTF-8''" + encodedName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
