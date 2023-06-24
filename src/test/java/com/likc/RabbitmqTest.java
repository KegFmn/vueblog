package com.likc;

import com.likc.dto.BlogMqDTO;
import com.likc.entity.Blog;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;

@SpringBootTest
public class RabbitmqTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void message() {
        Blog blog = new Blog();
        blog.setId(100L);
        blog.setContent("内容");
        blog.setOriginal(0);
        blog.setTitle("标题");
        blog.setTypeId(25L);
        blog.setUpdated(LocalDateTime.now());
        BlogMqDTO blogMqDTO = new BlogMqDTO();
        blogMqDTO.setType("delete");
        blogMqDTO.setBlog(blog);
        rabbitTemplate.convertAndSend("topicExchange", "blog", blogMqDTO);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
