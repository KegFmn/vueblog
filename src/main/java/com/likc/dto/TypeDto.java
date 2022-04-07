package com.likc.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author likc
 * @description
 * @since 2022/4/3
 */
@Data
public class TypeDto {

    private Long id;

    @NotBlank(message = "类型不能为空")
    private String typeName;
}
