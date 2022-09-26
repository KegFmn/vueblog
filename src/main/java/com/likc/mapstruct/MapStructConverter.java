package com.likc.mapstruct;

import com.likc.entity.Blog;
import com.likc.entity.Type;
import com.likc.entity.User;
import com.likc.vo.BlogLikeVo;
import com.likc.vo.BlogSimpleVo;
import com.likc.vo.TypeVo;
import com.likc.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author likc
 * @date 2022/8/15
 * @description
 */
@Mapper(componentModel = "spring")
public interface  MapStructConverter {

    MapStructConverter INSTANCE = Mappers.getMapper(MapStructConverter.class);

    BlogSimpleVo simpleEntity2Vo(Blog blog);

    UserVo userEntity2Vo(User user);

    TypeVo typeEntity2Vo(Type type);

}
