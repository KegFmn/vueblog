package com.likc.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author likc
 * @description
 * @since 2022/4/3
 */
@Data
public class BlogDto {

    private Long id;

    @NotNull(message = "类型不能为空")
    private Long typeId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "摘要不能为空")
    private String description;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "文章是否原创不能为空")
    private Integer original;
}
