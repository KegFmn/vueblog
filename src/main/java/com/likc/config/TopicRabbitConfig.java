package com.likc.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitConfig {
    private final static String BLOG_QUEUE = "blog";
    private final static String TOPIC = "topicExchange";

    public static final String DEAD_QUEUE = "dead";
    public static final String DEAD_EXCHANGE = "deadExchange";


    /**
     * 死信队列
     * @return
     */
    @Bean
    public Queue deadQueue(){
        return QueueBuilder.durable(DEAD_QUEUE).build();
    }
    @Bean
    public TopicExchange deadExchange(){
        return ExchangeBuilder.topicExchange(DEAD_EXCHANGE).durable(true).build();
    }
    @Bean
    public Binding bindingDeadExchangeMessage() {
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(DEAD_QUEUE);
    }

    /**
     * 正常队列
     * @return
     */
    @Bean
    public Queue blogQueue() {
        return QueueBuilder.durable(BLOG_QUEUE).deadLetterExchange(DEAD_EXCHANGE).deadLetterRoutingKey(DEAD_QUEUE).build();
    }
    @Bean
    public TopicExchange topicExchange() {
        return ExchangeBuilder.topicExchange(TOPIC).durable(true).build();
    }
    @Bean
    public Binding bindingBlogExchangeMessage() {
        return BindingBuilder.bind(blogQueue()).to(topicExchange()).with(BLOG_QUEUE);
    }

}
