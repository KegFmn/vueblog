package com.likc.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter messageConverter() {
        // 创建一个Jackson2JsonMessageConverter对象
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        // 创建一个RabbitTemplate对象
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 设置消息转换器为Jackson2JsonMessageConverter
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
