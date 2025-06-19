package com.be.byeoldam.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class RabbitMQConfig {

    private static final String QUEUE_NAME = "notification.queue";
    private static final String EXCHANGE_NAME = "notification.exchange";
    private static final String ROUTING_KEY = "notification.key";

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    // Message가 Queue에 도착했는지 확인하기 위한 변수
    public static final Map<String, CompletableFuture<Boolean>> returnFutureMap = new ConcurrentHashMap<>();

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE_NAME, true);  // durable=true -> 서버 재시작해도 큐 유지
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            String correlationId = correlationData != null ? correlationData.getId() : "NULL";
            CompletableFuture<Boolean> future = RabbitMQConfig.returnFutureMap.get(correlationId);

            if(ack){
                log.info("[ConfirmCallback] 메시지 전송 성공, correlationId = {}", correlationId);
                Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                    if (!future.isDone()) {
                        future.complete(true);
                    }
                }, 300, TimeUnit.MILLISECONDS);
            }else{
                log.error("[ConfirmCallback] 메시지 전송 실패, correlationId = {}", correlationId);
                log.error("원인: {}", cause);

                // Message 전송 실패 시 필요한 추가 로직은 여기에 작성하기.
                future.complete(false);
            }

            RabbitMQConfig.returnFutureMap.remove(correlationId);
        });
        
        // 해당 메소드는 라우팅 실패 시에만 호출되기 때문에 false를 저장
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            MessageProperties props = returnedMessage.getMessage().getMessageProperties();
            String correlationId = props != null ? props.getCorrelationId() : null;
            CompletableFuture<Boolean> future = RabbitMQConfig.returnFutureMap.get(correlationId);

            if (future != null) {
                future.complete(false);
                RabbitMQConfig.returnFutureMap.remove(correlationId);
            }
            log.error("[ReturnCallback] 메시지 전송 실패, correlationId = {}", correlationId);
        });

        return rabbitTemplate;
    }


}
