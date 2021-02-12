package com.sample.reqmanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;



@Configuration
//@PropertySource("file:config/rabbitmq-config.properties")
@PropertySource("classpath:rabbitmq-config.properties")
public class RabbitMQConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfiguration.class);

    @Autowired
    private Environment env;

    @Bean(name="productQueue")
    public Queue productQueue(){
        Queue queue = new Queue(env.getProperty("spring.rabbitmq.queue.name"),
                Boolean.valueOf(env.getProperty("spring.rabbitmq.queue.durable")).booleanValue(),
                Boolean.valueOf(env.getProperty("spring.rabbitmq.queue.exclusive")).booleanValue(),
                Boolean.valueOf(env.getProperty("spring.rabbitmq.queue.autoDelete")).booleanValue());
        return queue;
    }

   @Bean(name="productExchange")
    public DirectExchange productExchange() {
        DirectExchange exchange=new DirectExchange(env.getProperty("spring.rabbitmq.queue.exchange.name"));
        return exchange;
    }

    @Bean(name="productRouting")
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(env.getProperty("spring.rabbitmq.queue.routing.key"));
    }

    @Bean(name="connectionFactory")
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(env.getProperty("spring.rabbitmq.host"));
        factory.setPort(Integer.valueOf(env.getProperty("spring.rabbitmq.port")).intValue());
        factory.setUsername(env.getProperty("spring.rabbitmq.username"));
        factory.setPassword(env.getProperty("spring.rabbitmq.password"));
        factory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        factory.setConnectionTimeout(Integer.valueOf(env.getProperty("spring.rabbitmq.connectionTimeoutInMillis")).intValue());
        return factory;
    }

    @Bean(name="messageConverter")
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        return converter;
    }

    @Bean(name="amqpTemplate")
    public AmqpTemplate rabbitTemplate(){
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }

    //for consumer
    @Bean(name="containerFactory")
    public SimpleRabbitListenerContainerFactory containerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(messageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConcurrentConsumers(Integer.valueOf(env.getProperty("spring.rabbitmq.concurrentNumberOfConsumers")));
        factory.setMaxConcurrentConsumers(Integer.valueOf(env.getProperty("spring.rabbitmq.maxNumberOfConsumers")));
        return factory;
    }
}
