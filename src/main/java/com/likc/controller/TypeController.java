package com.likc.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.likc.common.lang.Result;
import com.likc.entity.Type;
import com.likc.service.TypeService;
import com.likc.util.ShiroUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class TypeController {

    @Autowired
    private TypeService typeService;


    @GetMapping("/types")
    public Result list(){
        QueryWrapper<Type> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0)
                .select("tid","type_name")
                .orderByAsc("created");

        List list = typeService.list(queryWrapper);

        return Result.succ(list);
    }

    @RequiresAuthentication
    @PostMapping("/type/save")
    public Result save(@RequestBody Type type){

        Type temp = null;
        QueryWrapper<Type> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("typename",type.getTypeName());

        temp = typeService.getOne(queryWrapper, false);

        Assert.isNull(temp,"该类型已存在");

        temp = new Type();
        temp.setUserId(ShiroUtil.getProfile().getUid());
        temp.setCreated(LocalDateTime.now());
        temp.setStatus(0);

        BeanUtil.copyProperties(type,temp,"id","userId","created","status");
        typeService.save(temp);

        return Result.succ(null);
    }

    @RequiresAuthentication
    @GetMapping("type/delete")
    public Result delete(@RequestParam(name = "typename") String typename){
        QueryWrapper<Type> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("typename", typename);
        typeService.remove(queryWrapper);

        return Result.succ(null);
    }


}
