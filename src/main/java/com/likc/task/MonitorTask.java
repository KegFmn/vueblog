package com.likc.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.likc.entity.Monitor;
import com.likc.service.MonitorService;
import com.likc.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void monitorTask() {
        log.info("定时任务写入数据库开始");
        Monitor monitor = new Monitor();

        monitor.setVisitTotal(Long.valueOf(redisUtils.get("visitTotal").toString()));
        monitor.setBlessTotal(Long.valueOf(redisUtils.get("blessTotal").toString()));
        monitor.setBlogTotal(Long.valueOf(redisUtils.get("blogTotal").toString()));

        QueryWrapper<Monitor> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        monitorService.update(monitor, wrapper);

        log.info("定时任务写入数据库成功");
    }
}
