package com.likc;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;

@SpringBootTest
public class RabbitmqTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void message() {
        HashMap<String, String> save = new HashMap<>();
        save.put("save","save");
        rabbitTemplate.convertAndSend("topicExchange", "blog.save", save);
        HashMap<String, String> update = new HashMap<>();
        update.put("update","update");
        rabbitTemplate.convertAndSend("topicExchange", "blog.update", update);
        HashMap<String, String> delete = new HashMap<>();
        delete.put("delete","delete");
        rabbitTemplate.convertAndSend("topicExchange", "blog.delete", delete);
    }
}
