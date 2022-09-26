package com.likc.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.likc.common.lang.Result;
import com.likc.dto.LikeDto;
import com.likc.entity.Blog;
import com.likc.entity.Like;
import com.likc.mapstruct.MapStructConverter;
import com.likc.service.BlogService;
import com.likc.service.LikeService;
import com.likc.util.RedisUtils;
import com.likc.vo.BlogLikeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author likc
 * @since 2022-09-23
 */
@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private MapStructConverter mapStructConverter;

    @Autowired
    private RedisUtils redisUtils;

    private static final String MAP_USER_LIKED = "MAP_USER_LIKED";

    @PostMapping("clickLike")
    public Result<BlogLikeVo> giveLike(@Validated @RequestBody LikeDto likeDto, @RequestHeader("fingerprint") String fingerprint) {
        if (StringUtils.isEmpty(fingerprint)){
            return new Result<>(400, "请刷新再使用哦");
        }
        String key = fingerprint + "::" + likeDto.getBlogId();
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            Object value = redisUtils.hget(MAP_USER_LIKED, key);
            if (likeDto.getType().equals(value)) {
                return new Result<>(400, "点赞的姿势不对哦");
            } else {
                redisUtils.hset(MAP_USER_LIKED, key, likeDto.getType(), 86400);
            }
        } finally {
            lock.unlock();
        }

        Like like = new Like();
        like.setUserId(fingerprint)
                .setBlogId(likeDto.getBlogId())
                .setType(likeDto.getType())
                .setCreated(LocalDateTime.now())
                .setUpdated(LocalDateTime.now())
                .setStatus(0);

        likeService.save(like);

        UpdateWrapper<Blog> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", likeDto.getBlogId());
        String msg;
        if (likeDto.getType() == 0) {
            wrapper.setSql("like_number = like_number + 1");
            msg = "谢谢你的赞";
        } else {
            wrapper.setSql("like_number = like_number - 1");
            msg = "取消点赞";
        }
        blogService.update(wrapper);

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", likeDto.getBlogId());
        Blog blog = blogService.getOne(queryWrapper);
        BlogLikeVo blogLikeVo = mapStructConverter.blogEntity2vo(blog);
        blogLikeVo.setType(likeDto.getType());

        return new Result<>(200, msg, blogLikeVo);
    }
}
