package com.software.testing.submitmodule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_submission")
public class Submission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long studentId;
    private String codeText;           // 学生提交的代码文本
    private String fileUrl;            // 上传文件的访问URL路径
    private String filePath;           // 提交文件存储路径
    private String fileName;           // 提交文件名
    private Integer status;            // 0-未提交 1-已提交 2-已批改
    private Integer score;             // 评分 0-100
    private String comment;            // 评语
    private LocalDateTime submitTime;
    private LocalDateTime reviewTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
