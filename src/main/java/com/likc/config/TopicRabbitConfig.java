package com.likc.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitConfig {
    private final static String BLOG = "blog";
    private final static String TOPIC = "topicExchange";

    @Bean
    public Queue blogQueue() {
        return new Queue(TopicRabbitConfig.BLOG);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC);
    }

    @Bean
    public Binding bindingSaveExchangeMessage() {
        return BindingBuilder.bind(blogQueue()).to(topicExchange()).with(BLOG);
    }

}
