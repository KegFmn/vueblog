package com.likc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.likc.entity.Blog;


public interface BlogService extends IService<Blog> {

    public Blog selectBlog(Long id);

}
