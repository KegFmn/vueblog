package com.likc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updated;
}
