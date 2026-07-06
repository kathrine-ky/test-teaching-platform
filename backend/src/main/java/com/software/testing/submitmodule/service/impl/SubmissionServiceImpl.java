package com.software.testing.submitmodule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.software.testing.common.Result;
import com.software.testing.scoremodule.dto.ReviewDTO;
import com.software.testing.submitmodule.entity.Submission;
import com.software.testing.submitmodule.mapper.SubmissionMapper;
import com.software.testing.submitmodule.service.SubmissionService;
import com.software.testing.taskmodule.entity.ExperimentTask;
import com.software.testing.taskmodule.mapper.ExperimentTaskMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    @Autowired
    private SubmissionMapper submissionMapper;
    @Autowired
    private ExperimentTaskMapper taskMapper;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public Result<?> submit(Long taskId, Long studentId, String codeText, MultipartFile file) {
        log.info("[提交作业] taskId={}, studentId={}, hasFile={}, hasCodeText={}",
                taskId, studentId, file != null && !file.isEmpty(), codeText != null && !codeText.isEmpty());
        ExperimentTask task = taskMapper.selectById(taskId);
        if (task == null) {
            log.warn("[任务不存在] taskId={}", taskId);
            return Result.error("任务不存在");
        }
        if (task.getDeadline() != null && LocalDateTime.now().isAfter(task.getDeadline())) {
            log.warn("[超过截止时间] taskId={}, studentId={}", taskId, studentId);
            return Result.error("已超过提交截止时间");
        }
        // 查找已有提交记录
        Submission submission = submissionMapper.selectOne(new LambdaQueryWrapper<Submission>()
                .eq(Submission::getTaskId, taskId)
                .eq(Submission::getStudentId, studentId));
        boolean isNew = (submission == null);
        if (isNew) {
            submission = new Submission();
            submission.setTaskId(taskId);
            submission.setStudentId(studentId);
            log.info("[新建提交记录] taskId={}, studentId={}", taskId, studentId);
        } else {
            log.info("[更新提交记录] submissionId={}, 已有fileUrl={}, 已有fileName={}",
                    submission.getId(), submission.getFileUrl(), submission.getFileName());
        }

        // 处理文件上传 - 关键修复：确保 fileUrl/filePath/fileName 正确写入
        if (file != null && !file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                log.info("[接收文件] 原始文件名={}, 文件大小={}bytes, contentType={}",
                        originalFilename, file.getSize(), file.getContentType());
                String relativePath = saveFile(file, "submissions/" + taskId + "/" + studentId);
                log.info("[文件保存成功] 存储路径={}", relativePath);

                // 确保 fileUrl/filePath/fileName 全部设置（非空判断后强制赋值）
                submission.setFilePath(relativePath);
                submission.setFileUrl(relativePath);
                submission.setFileName(originalFilename != null ? originalFilename : "unknown_file");
                log.info("[实体设置完成] filePath={}, fileUrl={}, fileName={}",
                        submission.getFilePath(), submission.getFileUrl(), submission.getFileName());
            } catch (IOException e) {
                log.error("[文件保存失败] taskId={}, studentId={}", taskId, studentId, e);
                return Result.error("文件保存失败: " + e.getMessage());
            }
        } else {
            log.info("[无文件上传] 跳过文件处理");
            // 如果是更新且没有新文件，保留原有的文件信息（不做任何设置）
            // 注意：新建记录且无文件时，fileUrl/filePath/fileName 保持 null
        }

        // 存储代码文本（仅在非空时设置，避免覆盖已有值）
        if (codeText != null && !codeText.isEmpty()) {
            submission.setCodeText(codeText);
            log.info("[代码文本设置] 长度={}", codeText.length());
        } else if (isNew) {
            submission.setCodeText(null);
        }

        submission.setStatus(1);
        submission.setSubmitTime(LocalDateTime.now());

        if (isNew) {
            log.info("[执行插入] fileUrl={}, fileName={}", submission.getFileUrl(), submission.getFileName());
            submissionMapper.insert(submission);
        } else {
            log.info("[执行更新] id={}, fileUrl={}, fileName={}", submission.getId(), submission.getFileUrl(), submission.getFileName());
            submissionMapper.updateById(submission);
        }

        // 重新查询确认数据库中的值
        Submission verify = submissionMapper.selectById(submission.getId());
        log.info("[提交作业成功-验证] submissionId={}, taskId={}, studentId={}, codeText={}, fileUrl={}, fileName={}, filePath={}",
                verify.getId(), verify.getTaskId(), verify.getStudentId(),
                verify.getCodeText() != null ? "有(" + verify.getCodeText().length() + "字)" : "无",
                verify.getFileUrl(),
                verify.getFileName(),
                verify.getFilePath());

        return Result.success("提交成功", verify);
    }

    @Override
    public Result<?> getMySubmission(Long taskId, Long studentId) {
        log.info("[查询我的提交] taskId={}, studentId={}", taskId, studentId);
        Submission submission = submissionMapper.selectOne(new LambdaQueryWrapper<Submission>()
                .eq(Submission::getTaskId, taskId)
                .eq(Submission::getStudentId, studentId));
        if (submission != null) {
            log.info("[我的提交详情] id={}, codeText={}, fileUrl={}, fileName={}, filePath={}",
                    submission.getId(),
                    submission.getCodeText() != null ? "有(" + submission.getCodeText().length() + "字)" : "无",
                    submission.getFileUrl(),
                    submission.getFileName(),
                    submission.getFilePath());
        } else {
            log.info("[我的提交详情] 未找到提交记录");
        }
        ExperimentTask task = taskMapper.selectById(taskId);
        Map<String, Object> result = new HashMap<>();
        result.put("submission", submission);
        result.put("deadline", task != null ? task.getDeadline() : null);
        return Result.success(result);
    }

    @Override
    public Result<?> getSubmissionsByTask(Long taskId) {
        log.info("[查询任务提交列表] taskId={}", taskId);
        List<Map<String, Object>> list = submissionMapper.selectWithStudentByTaskId(taskId);
        log.info("[查询任务提交列表结果] taskId={}, count={}", taskId, list.size());
        for (Map<String, Object> item : list) {
            log.info("[提交记录详情] id={}, studentName={}, codeText={}, fileUrl={}, fileName={}, filePath={}",
                    item.get("id"), item.get("studentName"),
                    item.get("codeText") != null ? "有" : "无",
                    item.get("fileUrl"),
                    item.get("fileName"),
                    item.get("filePath"));
        }
        return Result.success(list);
    }

    @Override
    public Result<?> review(ReviewDTO reviewDTO) {
        log.info("[批改作业] submissionId={}, score={}", reviewDTO.getSubmissionId(), reviewDTO.getScore());
        Submission submission = submissionMapper.selectById(reviewDTO.getSubmissionId());
        if (submission == null) {
            log.warn("[提交记录不存在] submissionId={}", reviewDTO.getSubmissionId());
            return Result.error("提交记录不存在");
        }
        submission.setScore(reviewDTO.getScore());
        submission.setComment(reviewDTO.getComment());
        submission.setStatus(2);
        submission.setReviewTime(LocalDateTime.now());
        submissionMapper.updateById(submission);
        log.info("[批改作业成功] submissionId={}, score={}", reviewDTO.getSubmissionId(), reviewDTO.getScore());
        return Result.success("批改成功", null);
    }

    @Override
    public Result<?> getScoreStatistics(Long taskId) {
        log.info("[查询成绩统计] taskId={}", taskId);
        List<Map<String, Object>> scores = submissionMapper.selectScoresByTaskId(taskId);
        if (scores.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("avgScore", 0);
            empty.put("maxScore", 0);
            empty.put("minScore", 0);
            empty.put("count", 0);
            empty.put("details", Collections.emptyList());
            return Result.success(empty);
        }
        double avg = scores.stream().mapToInt(s -> ((Number) s.get("score")).intValue()).average().orElse(0);
        int max = scores.stream().mapToInt(s -> ((Number) s.get("score")).intValue()).max().orElse(0);
        int min = scores.stream().mapToInt(s -> ((Number) s.get("score")).intValue()).min().orElse(0);
        Map<String, Object> stats = new HashMap<>();
        stats.put("avgScore", Math.round(avg * 100.0) / 100.0);
        stats.put("maxScore", max);
        stats.put("minScore", min);
        stats.put("count", scores.size());
        stats.put("details", scores);
        log.info("[查询成绩统计结果] taskId={}, count={}, avg={}", taskId, scores.size(), Math.round(avg * 100.0) / 100.0);
        return Result.success(stats);
    }

    @Override
    public byte[] exportScores(Long taskId) {
        log.info("[导出成绩] taskId={}", taskId);
        List<Map<String, Object>> scores = submissionMapper.selectScoresByTaskId(taskId);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("成绩表");
            Row header = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "班级", "成绩"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
            }
            for (int i = 0; i < scores.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, Object> s = scores.get(i);
                row.createCell(0).setCellValue(String.valueOf(s.getOrDefault("studentNo", "")));
                row.createCell(1).setCellValue(String.valueOf(s.getOrDefault("studentName", "")));
                row.createCell(2).setCellValue(String.valueOf(s.getOrDefault("className", "")));
                row.createCell(3).setCellValue(((Number) s.get("score")).intValue());
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            log.info("[导出成绩成功] taskId={}, rows={}", taskId, scores.size());
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("[导出成绩失败] taskId={}", taskId, e);
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    @Override
    public Result<?> getMyScores(Long studentId) {
        log.info("[查询我的成绩] studentId={}", studentId);
        List<Map<String, Object>> list = submissionMapper.selectByStudentId(studentId);
        return Result.success(list);
    }

    private String saveFile(MultipartFile file, String subDir) throws IOException {
        // 使用绝对路径确保文件写入正确位置
        File baseDir = new File(uploadDir);
        if (!baseDir.isAbsolute()) {
            baseDir = new File(System.getProperty("user.dir"), uploadDir);
        }
        File targetDir = new File(baseDir, subDir);
        if (!targetDir.exists()) {
            boolean created = targetDir.mkdirs();
            log.info("[创建目录] path={}, created={}", targetDir.getAbsolutePath(), created);
        }

        // 清理文件名中的特殊字符，保留中文
        String originalName = file.getOriginalFilename();
        String safeName = originalName.replaceAll("[\\\\/:*?\"<>|]", "_");
        String fileName = System.currentTimeMillis() + "_" + safeName;

        File dest = new File(targetDir, fileName);
        file.transferTo(dest);
        log.info("[文件写入磁盘] 绝对路径={}, 文件大小={}bytes, 文件存在={}",
                dest.getAbsolutePath(), dest.length(), dest.exists());

        // 返回相对于 uploadDir 的路径（用于URL访问）
        String relativePath = "/uploads/" + subDir + "/" + fileName;
        return relativePath;
    }
}
