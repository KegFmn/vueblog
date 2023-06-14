package com.likc;

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
        blog.setContent("测试");
        blog.setDescription("测试");
        blog.setOriginal(0);
        blog.setTitle("测试");
        blog.setTypeId(25L);
        blog.setUpdated(LocalDateTime.now());
//        HashMap<String, String> save = new HashMap<>();
//        save.put("save","save");
        rabbitTemplate.convertAndSend("topicExchange", "blog.save", blog);
//        HashMap<String, String> update = new HashMap<>();
//        update.put("update","update");
//        rabbitTemplate.convertAndSend("topicExchange", "blog.update", update);
//        HashMap<String, String> delete = new HashMap<>();
//        delete.put("delete","delete");
//        rabbitTemplate.convertAndSend("topicExchange", "blog.delete", delete);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
