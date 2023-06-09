package com.likc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.likc.common.lang.Result;
import com.likc.dto.TypeDto;
import com.likc.entity.Blog;
import com.likc.entity.Type;
import com.likc.mapstruct.MapStructConverter;
import com.likc.service.BlogService;
import com.likc.service.TypeService;
import com.likc.util.UserThreadLocal;
import com.likc.vo.TypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class TypeController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private MapStructConverter mapStructConverter;


    @GetMapping("/types")
    public Result<List<TypeVo>> list(){
        QueryWrapper<Type> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0)
                .select("id","type_name")
                .orderByDesc("created");

        List<Type> list = typeService.list(queryWrapper);

        List<TypeVo> typeVos = list.stream().map(item -> mapStructConverter.typeEntity2Vo(item)).collect(Collectors.toList());

        return new Result<>(200, "请求成功", typeVos);
    }


    @PostMapping("/type/save")
    public Result<Void> save(@Validated @RequestBody TypeDto typeDto){

        QueryWrapper<Type> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("BINARY type_name", typeDto.getTypeName());

        Type tempType = typeService.getOne(queryWrapper, false);

        Assert.isNull(tempType,"该类型已存在");

        Type temp = null;
        if (typeDto.getId() != null){
            temp = typeService.getById(typeDto.getId());
            //只能编辑自己的文章
            Assert.isTrue(temp.getUserId().longValue() == UserThreadLocal.get().getId().longValue(),"没有权限编辑");
        }else {
            temp = new Type();
            temp.setUserId(UserThreadLocal.get().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);
        }

        temp.setTypeName(typeDto.getTypeName());

        typeService.saveOrUpdate(temp);

        return new Result<>(200, "保存成功");
    }


    @PostMapping("type/delete")
    public Result<Void> delete(@Validated @RequestBody TypeDto typeDto){
        QueryWrapper<Type> typeWrapper = new QueryWrapper<>();
        typeWrapper.eq("BINARY type_name", typeDto.getTypeName());
        typeService.remove(typeWrapper);

        QueryWrapper<Blog> blogWrapper = new QueryWrapper<>();
        blogWrapper.eq("type_id", typeDto.getId());

        blogService.remove(blogWrapper);

        return new Result<>(200, "删除成功");
    }


}
