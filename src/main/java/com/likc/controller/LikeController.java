package com.likc.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.likc.annotation.AccessLimit;
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

    @AccessLimit(seconds = 8, maxCount = 5, needFingerprint = true)
    @PostMapping("clickLike")
    public Result<Integer> giveLike(@Validated @RequestBody LikeDto likeDto, @RequestHeader("fingerprint") String fingerprint) {
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

        if (likeDto.getType() == 0) {
            redisUtils.incr("likeTotal", 1);
        } else {
            redisUtils.decr("likeTotal", 1);
        }

        return new Result<>(200, msg, likeDto.getType());
    }
}
