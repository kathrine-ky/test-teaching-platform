package com.software.testing.taskmodule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_task_file")
public class TaskFile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String fileName;       // 原始文件名
    private String filePath;       // 存储路径
    private String fileUrl;        // 访问URL
    private Long fileSize;         // 文件大小(字节)
    private String fileType;       // 文件类型(如pdf, docx, xlsx)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
