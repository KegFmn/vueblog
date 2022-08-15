package com.likc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.likc.entity.Blog;
import com.likc.vo.BlogDetailsVo;


public interface BlogService extends IService<Blog> {

    BlogDetailsVo selectBlog(Long id);

}
