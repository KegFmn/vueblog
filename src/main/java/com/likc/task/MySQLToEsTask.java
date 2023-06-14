package com.likc.task;


import com.likc.entity.Blog;
import com.likc.service.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class MySQLToEsTask {

    @Resource
    private BlogService blogService;

//    @Scheduled(cron = "0 0 0/1 * * ?")
//    public void monitorTask() {
//        List<Blog> blogs = blogService.lambdaQuery().list();
//        for (Blog blog : blogs) {
//
//        }
//    }
}
