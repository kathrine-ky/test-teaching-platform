package com.software.testing.taskmodule.controller;

import com.software.testing.common.Result;
import com.software.testing.taskmodule.dto.TaskQueryDTO;
import com.software.testing.taskmodule.entity.ExperimentTask;
import com.software.testing.taskmodule.service.ExperimentTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/task")
public class ExperimentTaskController {

    @Autowired
    private ExperimentTaskService taskService;

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
}
