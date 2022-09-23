package com.likc.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author likc
 * @date 2022/9/24
 * @description
 */
@NoArgsConstructor
@Data
public class BlogLikeVo {

    private Integer type;
    private Long likeNumber;
}
