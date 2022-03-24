package com.likc.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.likc.common.lang.Result;
import com.likc.entity.Blog;
import com.likc.service.BlogService;
import com.likc.util.ShiroUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

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

    @GetMapping("blogs")
    public Result list(@RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                       @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
                       @RequestParam(name = "typeId", defaultValue = "0") Long typeId ){

        Page page = new Page(currentPage, pageSize);

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(typeId != 0, "type_id", typeId)
                .orderByDesc("created");


        IPage<Blog> pageDate = blogService.page(page,queryWrapper);

        return Result.succ(pageDate);
    }

    @GetMapping("blog/{id}")
    public Result detail(@PathVariable(name = "id") Long id){

        Blog blog = blogService.selectBlog(id);
        Assert.notNull(blog,"该博客已被删除");

        return Result.succ(blog);
    }

    @RequiresAuthentication
    @PostMapping("blog/edit")
    public Result edit(@Validated @RequestBody Blog blog){

        Blog temp = null;
        if (blog.getId() != null){
            temp = blogService.getById(blog.getId());
            //只能编辑自己的文章
            Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getUid().longValue(),"没有权限编辑");

        }else {
            temp = new Blog();
            temp.setUserId(ShiroUtil.getProfile().getUid());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);

        }

        BeanUtil.copyProperties(blog,temp,"id","userId","created","status");
        blogService.saveOrUpdate(temp);

        return Result.succ(null);
    }

    @RequiresAuthentication
    @PostMapping("blog/delete")
    public Result delete(@RequestBody Blog blog){

        Blog temp = blogService.getById(blog.getId());
        Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getUid().longValue(),"没有权限删除");
        blogService.removeById(temp.getId());

        return Result.succ(null);

    }

}
