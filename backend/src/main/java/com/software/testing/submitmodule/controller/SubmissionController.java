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
                            @RequestParam(required = false) String codeText,
                            @RequestParam(required = false) MultipartFile file,
                            HttpServletRequest request) {
        Long studentId = (Long) request.getAttribute("userId");
        return submissionService.submit(taskId, studentId, codeText, file);
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

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String path) {
        try {
            File file = new File("./" + path);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
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
