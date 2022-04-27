package com.likc.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.likc.common.lang.Result;
import com.likc.dto.BlogDto;
import com.likc.entity.Blog;
import com.likc.service.BlogService;
import com.likc.util.RedisUtils;
import com.likc.util.ShiroUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author likc
 * @since 2021-12-02
 */
@RestController
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("blogs")
    public Result<IPage<Blog>> list(@RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                       @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
                       @RequestParam(name = "typeId", defaultValue = "0") Long typeId ){

        Page<Blog> page = new Page<>(currentPage, pageSize);

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(typeId != 0, "type_id", typeId)
                .orderByDesc("updated");


        IPage<Blog> pageDate = blogService.page(page, queryWrapper);

        return new Result<>(200, "请求成功", pageDate);
    }

    @GetMapping("blog/{id}")
    public Result<Blog> detail(@PathVariable(name = "id") Long id){

        Blog blog = blogService.selectBlog(id);
        Assert.notNull(blog,"该博客已被删除");

        return new Result<>(200, "请求成功", blog);
    }

    @RequiresAuthentication
    @PostMapping("blog/edit")
    public Result<Void> edit(@Validated @RequestBody BlogDto blogDto){

        boolean flag = true;
        Blog temp = null;
        if (blogDto.getId() != null){
            temp = blogService.getById(blogDto.getId());
            temp.setUpdated(LocalDateTime.now());
            //只能编辑自己的文章
            Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getId().longValue(),"没有权限编辑");
            flag = false;
        }else {
            temp = new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setUpdated(LocalDateTime.now());
            temp.setStatus(0);
        }

        BeanUtils.copyProperties(blogDto, temp, "id","userId","created","updated","status");
        blogService.saveOrUpdate(temp);

        if (flag) {
            redisUtils.incr("blogTotal", 1);
        }

        return new Result<>(200, "保存成功");
    }

    @RequiresAuthentication
    @PostMapping("blog/delete")
    public Result<Void> delete(@RequestBody Blog blog){

        Blog temp = blogService.getById(blog.getId());
        Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getId().longValue(),"没有权限删除");
        blogService.removeById(temp.getId());

        redisUtils.decr("blogTotal", 1);

        return new Result<>(200, "删除成功");

    }

}
