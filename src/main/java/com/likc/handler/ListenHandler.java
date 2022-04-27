package com.likc.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.likc.entity.Blog;
import com.likc.entity.Monitor;
import com.likc.service.BlogService;
import com.likc.service.MonitorService;
import com.likc.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * @author likc
 * @since 2022/4/18
 * @description
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ListenHandler {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private BlogService blogService;

    public ListenHandler(){
        log.info("开始初始化");
    }

    @PostConstruct
    public void init() {
        log.info("监控数据初始化");

        List<Monitor> monitors = monitorService.list();

        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq("status", 0);
        int blogConut = blogService.count(blogQueryWrapper);

        Monitor monitor = null;
        if (monitors.size() > 0) {
            log.info("数据库已有数据");
            monitor = monitors.get(0);
        } else {
            log.info("数据库没有数据");
            monitor = new Monitor();
            monitor.setVisitTotal(0L);
            monitor.setBlessTotal(0L);
            monitor.setBlogTotal(0L);
            monitor.setStatus(0);
            monitorService.save(monitor);
        }
        // 访客总数
        redisUtils.incr("visitTotal", monitor.getVisitTotal());
        // 留言总数
        redisUtils.incr("blessTotal", monitor.getBlessTotal());
        // 博客总数
        redisUtils.incr("blogTotal", blogConut);

        log.info("写入redis成功");

    }

    @PreDestroy
    public void destroy(){
        log.info("系统关闭前");

        //将Redis中的数据取出写入数据库
        Monitor monitor = new Monitor();

        monitor.setVisitTotal(Long.valueOf(redisUtils.get("visitTotal").toString()));
        monitor.setBlessTotal(Long.valueOf(redisUtils.get("blessTotal").toString()));
        monitor.setBlogTotal(Long.valueOf(redisUtils.get("blogTotal").toString()));

        QueryWrapper<Monitor> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        monitorService.update(monitor, wrapper);

        log.info("缓存数据保存数据库成功");

        redisUtils.del("visitTotal","blessTotal","blogTotal");

        log.info("删除缓存visitTotal、blessTotal、blogTotal的Key成功");
    }
}
