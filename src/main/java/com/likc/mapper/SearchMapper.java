package com.likc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.likc.search.CollectDoc;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author likc
 * @date 2022/5/25
 * @description
 */
@Mapper
public interface SearchMapper extends BaseMapper<CollectDoc> {
}
