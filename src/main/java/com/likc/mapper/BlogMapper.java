package com.likc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.likc.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

    public Blog selectBlog(Long id);

}
