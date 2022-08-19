package com.likc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updated;
    private Integer original;

}
