package com.likc.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likc.entity.Blog;
import com.likc.entity.Monitor;
import com.likc.service.BlogService;
import com.likc.service.MonitorService;
import com.likc.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    public ListenHandler(){
        log.info("开始初始化");
    }

    @PostConstruct
    public void init() {
        log.info("监控数据初始化");

        redisUtils.del("visitTotal","blessTotal","blogTotal");

        Monitor one = monitorService.getOne(new QueryWrapper<>(), false);

        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        int blogCount = blogService.count(blogQueryWrapper);
        
        RestTemplate restTemplate = new RestTemplate();
        StringBuilder builder = new StringBuilder("https://api.github.com");
        builder.append("/repos/KegFmn/vueblog-vuetify/issues/2");
        Map<String, Object> map = null;
        try {
            String object = restTemplate.getForObject(builder.toString(), String.class);
            map = objectMapper.readValue(object, Map.class);
        } catch (Exception e) {
            log.error("同步github评论失败，异常={}", e.getMessage());
        }
        int blessTotal = map.get("comments") == null ? 0 : (Integer) map.get("comments");

        Monitor monitor;
        if (one != null) {
            log.info("数据库已有监控数据={}, one.toString()");
            monitor = one;
        } else {
            log.info("数据库没有监控数据");
            monitor = new Monitor();
            monitor.setVisitTotal(0L);
            monitor.setBlessTotal(blessTotal);
            monitor.setBlogTotal(blogCount);
            monitor.setStatus(0);
            monitorService.save(monitor);
        }
        
        // 访客总数
        redisUtils.incr("visitTotal", monitor.getVisitTotal());
        // 留言总数
        redisUtils.incr("blessTotal", blessTotal == 0 ? monitor.getBlessTotal() : blessTotal);
        // 博客总数
        redisUtils.incr("blogTotal", blogCount);

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
