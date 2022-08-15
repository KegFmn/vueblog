package com.likc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.likc.entity.Blog;
import com.likc.vo.BlogDetailsVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

    BlogDetailsVo selectBlog(Long id);

}
