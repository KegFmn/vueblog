package com.likc.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author likc
 * @date 2022/9/23
 * @description
 */
@Data
public class LikeDto {

    /**
     * 用户标识
     */
    @NotNull(message = "用户标识不能为空")
    private String userId;

    /**
     * 博客ID
     */
    @NotNull(message = "博客ID不能为空")
    private Long blogId;

    /**
     * 点击类型(点赞【0】还是取消点赞【1】)
     */
    @NotNull(message = "类型不能为空")
    private Integer type;
}
