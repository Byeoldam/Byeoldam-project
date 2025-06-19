package com.be.byeoldam.domain.notification.event;

import com.be.byeoldam.config.RabbitMQConfig;
import com.be.byeoldam.domain.notification.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE_NAME = "notification.exchange";
    private static final String ROUTING_KEY = "notification.key";

    public void sendNotification(NotificationMessage message) {
        String correlationId = UUID.randomUUID().toString();

        // 라우팅 성공 여부를 저장
        CompletableFuture<Boolean> returnFuture = new CompletableFuture<>();
        RabbitMQConfig.returnFutureMap.put(correlationId, returnFuture);

        CorrelationData correlationData = new CorrelationData(correlationId);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message, msg -> {
            msg.getMessageProperties().setCorrelationId(correlationData.getId());
            return msg;
        },correlationData);

       try {
           boolean isAck = correlationData.getFuture().get(5, TimeUnit.SECONDS).isAck();
           if (!isAck) {
               throw new RuntimeException("Broker 전달 실패");
           }

           boolean isReturned = returnFuture.get(5, TimeUnit.SECONDS);
           if (!isReturned) { 
               throw new RuntimeException("Routing 실패");
           }
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
}
