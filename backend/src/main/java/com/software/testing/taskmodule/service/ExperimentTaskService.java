package com.software.testing.taskmodule.service;

import com.software.testing.common.Result;
import com.software.testing.taskmodule.dto.TaskQueryDTO;
import com.software.testing.taskmodule.entity.ExperimentTask;
import org.springframework.web.multipart.MultipartFile;

public interface ExperimentTaskService {
    Result<?> createTask(ExperimentTask task);
    Result<?> updateTask(ExperimentTask task);
    Result<?> deleteTask(Long taskId);
    Result<?> getTaskDetail(Long taskId);
    Result<?> listTasks(TaskQueryDTO query, Long userId, String role);
    Result<?> publishTask(Long taskId);
    /** 撤回已发布任务（状态改回草稿） */
    Result<?> withdrawTask(Long taskId);
    /** 上传任务附件（测试用例模板、环境说明等） */
    Result<?> uploadTaskFile(Long taskId, MultipartFile file);
    /** 获取任务附件列表 */
    Result<?> getTaskFiles(Long taskId);
    /** 删除任务附件 */
    Result<?> deleteTaskFile(Long fileId);
    /** 下载任务附件 */
    Result<?> downloadTaskFile(Long fileId);
}
