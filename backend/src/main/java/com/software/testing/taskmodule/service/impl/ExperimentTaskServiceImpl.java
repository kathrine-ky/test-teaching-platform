package com.software.testing.taskmodule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.software.testing.common.PageResult;
import com.software.testing.common.Result;
import com.software.testing.taskmodule.dto.TaskQueryDTO;
import com.software.testing.taskmodule.entity.ExperimentTask;
import com.software.testing.taskmodule.mapper.ExperimentTaskMapper;
import com.software.testing.taskmodule.service.ExperimentTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExperimentTaskServiceImpl implements ExperimentTaskService {

    private static final Logger log = LoggerFactory.getLogger(ExperimentTaskServiceImpl.class);

    @Autowired
    private ExperimentTaskMapper taskMapper;

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
        return Result.success(task);
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
}
