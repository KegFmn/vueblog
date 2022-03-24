package com.likc.service;

import com.likc.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;


public interface BlogService extends IService<Blog> {

    public Blog selectBlog(Long id);

}
