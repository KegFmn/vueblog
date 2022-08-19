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
        log.info("定时任务写入数据库开始");
        Monitor monitor = new Monitor();
        
        RestTemplate restTemplate = new RestTemplate();
        StringBuilder builder = new StringBuilder("https://api.github.com");
        builder.append("/repos/KegFmn/vueblog-vuetify/issues/2");
        Map<String, Object> map = null;
        try {
            String object = restTemplate.getForObject(builder.toString(), String.class);
            map = objectMapper.readValue(object, Map.class);
        } catch (Exception e) {
            log.error("定时同步github评论失败，异常={}", e.getMessage());
        }
        int blessTotal = map.get("comments") == null ? 0 : (Integer) map.get("comments");
        
        if (blessTotal != 0) {
            redisUtils.del("blessTotal");
            redisUtils.incr("blessTotal", blessTotal);
        }

        monitor.setVisitTotal(Long.valueOf(redisUtils.get("visitTotal").toString()));
        monitor.setBlessTotal(Long.valueOf(redisUtils.get("blessTotal").toString()));
        monitor.setBlogTotal(Long.valueOf(redisUtils.get("blogTotal").toString()));

        QueryWrapper<Monitor> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        monitorService.update(monitor, wrapper);

        log.info("定时任务写入数据库成功");
    }
}
