package com.likc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.likc.common.lang.Result;
import com.likc.dto.TypeDto;
import com.likc.entity.Blog;
import com.likc.entity.Type;
import com.likc.service.BlogService;
import com.likc.service.TypeService;
import com.likc.util.RedisUtils;
import com.likc.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
public class TypeController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private RedisUtils redisUtils;


    @GetMapping("/types")
    public Result<List<Type>> list(){
        QueryWrapper<Type> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0)
                .select("id","type_name")
                .orderByDesc("created");

        List<Type> list = typeService.list(queryWrapper);

        return new Result<>(200, "请求成功", list);
    }

    @RequiresAuthentication
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
            Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getId().longValue(),"没有权限编辑");
        }else {
            temp = new Type();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);
        }

        BeanUtils.copyProperties(typeDto, temp, "id","userId","created","status", "blogs");

        typeService.saveOrUpdate(temp);

        return new Result<>(200, "保存成功");
    }

    @RequiresAuthentication
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
