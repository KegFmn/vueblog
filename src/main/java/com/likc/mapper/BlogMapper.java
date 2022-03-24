package com.likc.mapper;

import com.likc.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

    public Blog selectBlog(Long id);

}
