package com.likc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author likc
 * @since 2022-09-23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("m_like")
public class Like implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户标识
     */
    @TableField("user_id")
    private String userId;

    /**
     * 博客ID
     */
    @TableField("blog_id")
    private Long blogId;

    /**
     * 创建时间
     */
    @TableField("created")
    private LocalDateTime created;

    /**
     * 更新时间
     */
    @TableField("updated")
    private LocalDateTime updated;

    /**
     * 类型
     */
    @TableField("type")
    private Integer type;

    /**
     * 状态
     */
    @TableLogic
    @TableField("status")
    private Integer status;


}
