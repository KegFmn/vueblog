package com.likc.dto;

import com.likc.entity.Blog;
import lombok.Data;

@Data
public class BlogMqDTO {
    private String type;
    private Blog blog;
}
