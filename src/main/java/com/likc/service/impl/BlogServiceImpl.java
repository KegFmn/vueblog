package com.likc.service.impl;

import com.likc.entity.Blog;
import com.likc.mapper.BlogMapper;
import com.likc.service.BlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.likc.vo.BlogDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private BlogMapper blogMapper;
    
    @Override
    public BlogDetailsVo selectBlog(Long id){
       return blogMapper.selectBlog(id);
    }
    
}
