package com.software.testing.scoremodule.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class ReviewDTO {
    @NotNull(message = "提交ID不能为空")
    private Long submissionId;
    @NotNull(message = "评分不能为空")
    private Integer score;
    private String comment;
}
