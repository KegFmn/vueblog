package com.likc.controller;


import com.likc.common.lang.Result;
import com.likc.entity.Monitor;
import com.likc.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author
 * @since 2022-04-18
 */
@RestController
public class MonitorController {

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/monitor")
    public Result<Monitor> getMonitor() {
        Monitor monitor = new Monitor();
        monitor.setVisitTotal(Long.valueOf(redisUtils.get("visitTotal").toString()));
        monitor.setBlessTotal(Long.valueOf(redisUtils.get("blessTotal").toString()));
        monitor.setBlogTotal(Long.valueOf(redisUtils.get("blogTotal").toString()));
        monitor.setLikeTotal(Long.valueOf(redisUtils.get("likeTotal").toString()));

        return new Result<>(200, "请求成功", monitor);
    }

//    @GetMapping("/monitor/addVisitTotal")
//    public Result<Void> addVisitTotal() {
//
//        redisUtils.incr("visitTotal",1);
//
//        return new Result<>(200, "请求成功");
//    }
}
