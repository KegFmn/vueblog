package com.likc.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.Objects;

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

        redisUtils.del("visitTotal","blessTotal","blogTotal","likeTotal");

        Monitor one = monitorService.lambdaQuery().one();

        int blogCount = blogService.lambdaQuery().count();
        log.info("博客总数:{}", blogCount);

        QueryWrapper<Blog> ew = new QueryWrapper<>();
        ew.select("IFNULL(sum(like_number),0) AS likeNum");
        Map<String, Object> blogMap = blogService.getMap(ew);
        String likeCount = String.valueOf(blogMap.get("likeNum"));
        log.info("点赞总数:{}", likeCount);

        RestTemplate restTemplate = new RestTemplate();
        StringBuilder builder = new StringBuilder("https://api.github.com");
        builder.append("/repos/KegFmn/vueblog-vuetify/issues/2");
        Map<String, Object> map = null;
        try {
            String object = restTemplate.getForObject(builder.toString(), String.class);
            if (Objects.nonNull(object)) {
                map = objectMapper.readValue(object, Map.class);
            }
        } catch (Exception e) {
            log.error("同步github评论失败，异常={}", e.getMessage());
        }
        Integer blessTotal = null;
        if (Objects.nonNull(map)) {
            blessTotal = (Integer) map.get("comments");
        }
        log.info("同步github评论成功，数量={}", blessTotal);

        Monitor monitor;
        if (Objects.nonNull(one)) {
            log.info("数据库已有监控数据={}", one);
            monitor = one;
        } else {
            log.info("数据库没有监控数据");
            monitor = new Monitor();
            monitor.setVisitTotal(0L);
            monitor.setBlessTotal(blessTotal != null ? blessTotal.longValue() : 0L);
            monitor.setBlogTotal((long) blogCount);
            monitor.setLikeTotal(Long.parseLong(likeCount));
            monitor.setStatus(0);
            monitorService.save(monitor);
        }
        
        // 访客总数
        redisUtils.incr("visitTotal", monitor.getVisitTotal());
        // 留言总数
        redisUtils.incr("blessTotal", blessTotal == null ? monitor.getBlessTotal() : blessTotal.longValue());
        // 博客总数
        redisUtils.incr("blogTotal", blogCount);
        // 博客总数
        redisUtils.incr("likeTotal", Long.parseLong(likeCount));

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
        monitor.setBlogTotal(Long.valueOf(redisUtils.get("likeTotal").toString()));
        monitorService.update(monitor, new LambdaQueryWrapper<Monitor>().eq(Monitor::getStatus, 0));
        log.info("缓存数据保存数据库成功");
        redisUtils.del("visitTotal","blessTotal","blogTotal","likeTotal");
        log.info("删除缓存visitTotal、blessTotal、blogTotal、likeTotal的Key成功");
    }
}
