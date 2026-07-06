package com.software.testing.taskmodule.service;

import com.software.testing.common.Result;
import com.software.testing.taskmodule.dto.TaskQueryDTO;
import com.software.testing.taskmodule.entity.ExperimentTask;

public interface ExperimentTaskService {
    Result<?> createTask(ExperimentTask task);
    Result<?> updateTask(ExperimentTask task);
    Result<?> deleteTask(Long taskId);
    Result<?> getTaskDetail(Long taskId);
    Result<?> listTasks(TaskQueryDTO query, Long userId, String role);
    Result<?> publishTask(Long taskId);
}
