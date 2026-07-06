package com.software.testing.taskmodule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_experiment_task")
public class ExperimentTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private String requirement;
    private Long teacherId;
    private LocalDateTime deadline;
    private Integer status;              // 0-草稿 1-已发布 2-已截止
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
