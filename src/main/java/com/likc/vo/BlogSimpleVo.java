package com.likc.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author likc
 * @date 2022/8/15
 * @description
 */
@NoArgsConstructor
@Data
public class BlogSimpleVo {

    private Integer id;
    private String title;
    private String description;
    private String updated;
}
