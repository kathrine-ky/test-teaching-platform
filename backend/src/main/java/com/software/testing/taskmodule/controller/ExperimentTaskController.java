package com.software.testing.taskmodule.controller;

import com.software.testing.common.Result;
import com.software.testing.taskmodule.dto.TaskQueryDTO;
import com.software.testing.taskmodule.entity.ExperimentTask;
import com.software.testing.taskmodule.entity.TaskFile;
import com.software.testing.taskmodule.mapper.TaskFileMapper;
import com.software.testing.taskmodule.service.ExperimentTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/task")
public class ExperimentTaskController {

    @Autowired
    private ExperimentTaskService taskService;

    @Autowired
    private TaskFileMapper taskFileMapper;

    @PostMapping("/create")
    public Result<?> create(@RequestBody ExperimentTask task, HttpServletRequest request) {
        task.setTeacherId((Long) request.getAttribute("userId"));
        return taskService.createTask(task);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody ExperimentTask task) {
        return taskService.updateTask(task);
    }

    @DeleteMapping("/{taskId}")
    public Result<?> delete(@PathVariable Long taskId) {
        return taskService.deleteTask(taskId);
    }

    @GetMapping("/{taskId}")
    public Result<?> detail(@PathVariable Long taskId) {
        return taskService.getTaskDetail(taskId);
    }

    @PostMapping("/list")
    public Result<?> list(@RequestBody TaskQueryDTO query, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        return taskService.listTasks(query, userId, role);
    }

    @PutMapping("/publish/{taskId}")
    public Result<?> publish(@PathVariable Long taskId) {
        return taskService.publishTask(taskId);
    }

    /** 撤回已发布任务（改回草稿） */
    @PutMapping("/withdraw/{taskId}")
    public Result<?> withdraw(@PathVariable Long taskId) {
        return taskService.withdrawTask(taskId);
    }

    /** 上传任务附件（测试用例模板、环境说明文档等） */
    @PostMapping("/{taskId}/file/upload")
    public Result<?> uploadFile(@PathVariable Long taskId,
                                @RequestParam("file") MultipartFile file) {
        return taskService.uploadTaskFile(taskId, file);
    }

    /** 获取任务附件列表 */
    @GetMapping("/{taskId}/files")
    public Result<?> getFiles(@PathVariable Long taskId) {
        return taskService.getTaskFiles(taskId);
    }

    /** 删除任务附件 */
    @DeleteMapping("/file/{fileId}")
    public Result<?> deleteFile(@PathVariable Long fileId) {
        return taskService.deleteTaskFile(fileId);
    }

    /** 下载任务附件 */
    @GetMapping("/file/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        TaskFile tf = taskFileMapper.selectById(fileId);
        if (tf == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            java.nio.file.Path filePath = Paths.get(tf.getFilePath());
            Resource resource = new FileSystemResource(filePath);
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            String encodedFileName = URLEncoder.encode(tf.getFileName(), "UTF-8")
                    .replaceAll("\\+", "%20");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename*=UTF-8''" + encodedFileName)
                    .body(resource);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
