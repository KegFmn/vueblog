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
public class BlogDetailsVo {

    private Integer id;
    private Integer userId;
    private String userName;
    private Long typeId;
    private String typeName;
    private String title;
    private String description;
    private String content;
    private String updated;
    private Integer original;

}
