package com.software.testing.taskmodule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.software.testing.common.PageResult;
import com.software.testing.common.Result;
import com.software.testing.taskmodule.dto.TaskQueryDTO;
import com.software.testing.taskmodule.entity.ExperimentTask;
import com.software.testing.taskmodule.entity.TaskFile;
import com.software.testing.taskmodule.mapper.ExperimentTaskMapper;
import com.software.testing.taskmodule.mapper.TaskFileMapper;
import com.software.testing.taskmodule.service.ExperimentTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ExperimentTaskServiceImpl implements ExperimentTaskService {

    private static final Logger log = LoggerFactory.getLogger(ExperimentTaskServiceImpl.class);

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Autowired
    private ExperimentTaskMapper taskMapper;

    @Autowired
    private TaskFileMapper taskFileMapper;

    @Override
    public Result<?> createTask(ExperimentTask task) {
        log.info("[创建任务] title={}, teacherId={}, courseId={}", task.getTitle(), task.getTeacherId(), task.getCourseId());
        task.setStatus(0);
        taskMapper.insert(task);
        log.info("[创建任务成功] id={}", task.getId());
        return Result.success("创建成功", task);
    }

    @Override
    public Result<?> updateTask(ExperimentTask task) {
        log.info("[更新任务] id={}, title={}", task.getId(), task.getTitle());
        taskMapper.updateById(task);
        log.info("[更新任务成功] id={}", task.getId());
        return Result.success("更新成功", task);
    }

    @Override
    public Result<?> deleteTask(Long taskId) {
        log.info("[删除任务] id={}", taskId);
        // 删除关联附件
        List<TaskFile> files = taskFileMapper.selectList(
                new LambdaQueryWrapper<TaskFile>().eq(TaskFile::getTaskId, taskId));
        for (TaskFile f : files) {
            try { Files.deleteIfExists(Paths.get(f.getFilePath())); } catch (IOException ignored) {}
        }
        taskFileMapper.delete(new LambdaQueryWrapper<TaskFile>().eq(TaskFile::getTaskId, taskId));
        taskMapper.deleteById(taskId);
        log.info("[删除任务成功] id={}", taskId);
        return Result.success("删除成功", null);
    }

    @Override
    public Result<?> getTaskDetail(Long taskId) {
        log.info("[查询任务详情] id={}", taskId);
        ExperimentTask task = taskMapper.selectById(taskId);
        if (task == null) {
            log.warn("[任务不存在] id={}", taskId);
            return Result.error("任务不存在");
        }
        // 附带附件列表
        List<TaskFile> files = taskFileMapper.selectList(
                new LambdaQueryWrapper<TaskFile>().eq(TaskFile::getTaskId, taskId));
        // 使用一个简单容器返回
        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("task", task);
        result.put("files", files);
        return Result.success(result);
    }

    @Override
    public Result<?> listTasks(TaskQueryDTO query, Long userId, String role) {
        log.info("[查询任务列表] userId={}, role={}, current={}, size={}", userId, role, query.getCurrent(), query.getSize());
        LambdaQueryWrapper<ExperimentTask> wrapper = new LambdaQueryWrapper<>();
        if (query.getTitle() != null && !query.getTitle().isEmpty()) {
            wrapper.like(ExperimentTask::getTitle, query.getTitle());
        }
        if (query.getCourseId() != null) {
            wrapper.eq(ExperimentTask::getCourseId, query.getCourseId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(ExperimentTask::getStatus, query.getStatus());
        }
        if ("TEACHER".equals(role)) {
            wrapper.eq(ExperimentTask::getTeacherId, userId);
        } else {
            wrapper.eq(ExperimentTask::getStatus, 1); // 学生只能看已发布
        }
        wrapper.orderByDesc(ExperimentTask::getCreateTime);
        Page<ExperimentTask> page = new Page<>(query.getCurrent(), query.getSize());
        Page<ExperimentTask> result = taskMapper.selectPage(page, wrapper);
        log.info("[查询任务列表结果] total={}, records={}", result.getTotal(), result.getRecords().size());
        PageResult<ExperimentTask> pageResult = new PageResult<>(
                result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
        return Result.success(pageResult);
    }

    @Override
    public Result<?> publishTask(Long taskId) {
        log.info("[发布任务] id={}", taskId);
        ExperimentTask task = taskMapper.selectById(taskId);
        if (task == null) {
            log.warn("[任务不存在] id={}", taskId);
            return Result.error("任务不存在");
        }
        task.setStatus(1);
        taskMapper.updateById(task);
        log.info("[发布任务成功] id={}", taskId);
        return Result.success("发布成功", null);
    }

    @Override
    public Result<?> withdrawTask(Long taskId) {
        log.info("[撤回任务] id={}", taskId);
        ExperimentTask task = taskMapper.selectById(taskId);
        if (task == null) {
            log.warn("[任务不存在] id={}", taskId);
            return Result.error("任务不存在");
        }
        if (task.getStatus() != 1) {
            return Result.error("只有已发布的任务才能撤回");
        }
        task.setStatus(0);
        taskMapper.updateById(task);
        log.info("[撤回任务成功] id={}", taskId);
        return Result.success("已撤回为草稿", null);
    }

    @Override
    public Result<?> uploadTaskFile(Long taskId, MultipartFile file) {
        log.info("[上传任务附件] taskId={}, fileName={}", taskId, file.getOriginalFilename());
        ExperimentTask task = taskMapper.selectById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }
        try {
            // 使用绝对路径确保文件写入正确位置（避免Tomcat临时目录问题）
            java.io.File baseDir = new java.io.File(uploadDir);
            if (!baseDir.isAbsolute()) {
                baseDir = new java.io.File(System.getProperty("user.dir"), uploadDir);
            }
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path uploadPath = Paths.get(baseDir.getAbsolutePath(), "task_files", dateDir);
            Files.createDirectories(uploadPath);

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String storedName = UUID.randomUUID().toString() + ext;
            Path destPath = uploadPath.resolve(storedName);
            file.transferTo(destPath.toFile());

            TaskFile tf = new TaskFile();
            tf.setTaskId(taskId);
            tf.setFileName(originalName);
            tf.setFilePath(destPath.toString());
            tf.setFileUrl("/uploads/task_files/" + dateDir + "/" + storedName);
            tf.setFileSize(file.getSize());
            tf.setFileType(ext.replace(".", ""));
            taskFileMapper.insert(tf);

            log.info("[上传附件成功] fileId={}, name={}", tf.getId(), originalName);
            return Result.success("上传成功", tf);
        } catch (IOException e) {
            log.error("[上传附件失败]", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public Result<?> getTaskFiles(Long taskId) {
        List<TaskFile> files = taskFileMapper.selectList(
                new LambdaQueryWrapper<TaskFile>().eq(TaskFile::getTaskId, taskId)
                        .orderByDesc(TaskFile::getCreateTime));
        return Result.success(files);
    }

    @Override
    public Result<?> deleteTaskFile(Long fileId) {
        TaskFile tf = taskFileMapper.selectById(fileId);
        if (tf != null) {
            try { Files.deleteIfExists(Paths.get(tf.getFilePath())); } catch (IOException ignored) {}
            taskFileMapper.deleteById(fileId);
        }
        return Result.success("删除成功", null);
    }

    @Override
    public Result<?> downloadTaskFile(Long fileId) {
        TaskFile tf = taskFileMapper.selectById(fileId);
        if (tf == null) {
            return Result.error("文件不存在");
        }
        java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("fileName", tf.getFileName());
        data.put("filePath", tf.getFilePath());
        data.put("fileUrl", tf.getFileUrl());
        return Result.success(data);
    }
}
