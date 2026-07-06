package com.software.testing.taskmodule.dto;

import lombok.Data;

@Data
public class TaskQueryDTO {
    private Long current = 1L;
    private Long size = 10L;
    private String title;
    private Integer status;
    private Long courseId;
}
