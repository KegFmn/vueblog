package com.likc.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitConfig {
    private final static String SAVE = "blog.save";
    private final static String DELETE = "blog.delete";
    private final static String TOPIC = "topicExchange";

    @Bean
    public Queue saveQueue() {
        return new Queue(TopicRabbitConfig.SAVE);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(TopicRabbitConfig.DELETE);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC);
    }

    @Bean
    public Binding bindingSaveExchangeMessage() {
        return BindingBuilder.bind(saveQueue()).to(topicExchange()).with(SAVE);
    }

    @Bean
    public Binding bindingDeleteExchangeMessage() {
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(DELETE);
    }

}
