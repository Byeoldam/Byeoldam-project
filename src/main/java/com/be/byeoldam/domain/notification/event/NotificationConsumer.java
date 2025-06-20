package com.be.byeoldam.domain.notification.event;

import com.be.byeoldam.domain.notification.dto.NotificationMessage;
import com.be.byeoldam.domain.notification.service.NotificationService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final RetryTemplate retryTemplate;
    private final NotificationService notificationService;

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    @RabbitListener(queues = "notification.queue", containerFactory = "rabbitListenerContainerFactory")
    public void receiveNotification(NotificationMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

       try {
           retryTemplate.execute(context -> {   //로직 수행
               String type = message.getType();
               if(!type.equals("INVITE") && !type.equals("BOOKMARK") ) {
                   throw new RuntimeException(String.format(
                           "Invalid message type received. type=%s, message=%s", type, message.toString()));
               }

               notificationService.processNotification(message);
               channel.basicAck(tag, false);

               return null;
           },context ->{    // 재시도까지 포함해서 로직 실패한 경우 처리
               log.error("DLQ로 이동할 메시지: {}", message);
               channel.basicNack(tag, false, false);

               return null;
           });
       }catch (Exception e) {
           // recoveryCallback에서 예외 던지지 않아서 실행 x
       }
    }
}
