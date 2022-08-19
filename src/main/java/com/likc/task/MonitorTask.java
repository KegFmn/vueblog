package com.likc.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likc.entity.Monitor;
import com.likc.service.MonitorService;
import com.likc.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author likc
 * @since 2022/4/18
 */
@Slf4j
@Component
public class MonitorTask {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MonitorService monitorService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void monitorTask() {
        Monitor monitor = new Monitor();

        RestTemplate restTemplate = new RestTemplate();
        StringBuilder builder = new StringBuilder("https://api.github.com");
        builder.append("/repos/KegFmn/vueblog-vuetify/issues/2");
        Map<String, Object> map = null;
        try {
            String object = restTemplate.getForObject(builder.toString(), String.class);
            if (object != null) {
                map = objectMapper.readValue(object, Map.class);
            }
        } catch (Exception e) {
            log.error("定时同步github评论失败，异常={}", e.getMessage());
        }
        Integer blessTotal = null;
        if (map != null) {
            blessTotal = (Integer) map.get("comments");
            if (blessTotal != null) {
                log.info("清除评论缓存");
                redisUtils.del("blessTotal");
                redisUtils.incr("blessTotal", blessTotal);
                log.info("重新设置评论缓存");;
            }
        }

        log.info("定时同步github评论数量={}", blessTotal);

        log.info("定时监控写入数据库开始");
        monitor.setVisitTotal(Long.valueOf(redisUtils.get("visitTotal").toString()));
        monitor.setBlessTotal(Long.valueOf(redisUtils.get("blessTotal").toString()));
        monitor.setBlogTotal(Long.valueOf(redisUtils.get("blogTotal").toString()));

        QueryWrapper<Monitor> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        monitorService.update(monitor, wrapper);

        log.info("定时监控写入数据库成功");
    }
}
