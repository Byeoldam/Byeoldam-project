package com.be.byeoldam.domain.notification.event;

import com.be.byeoldam.config.RabbitMQConfig;
import com.be.byeoldam.domain.notification.dto.NotificationMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class NotificationDlqConsumer {
    @RabbitListener(queues = RabbitMQConfig.DLQ, containerFactory = "rabbitListenerContainerFactory" )
    public void receiveFailedNotification(NotificationMessage message,
                                          Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.error("[DLQ] 메시지 수신: {}", message);
        // 추후에 로직 추가하기

        try{
            channel.basicAck(tag, false);
        }catch (Exception e){
            channel.basicNack(tag, false, false);
        }

    }
}
