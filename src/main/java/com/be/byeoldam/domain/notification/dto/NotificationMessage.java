package com.be.byeoldam.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RabbitMQ 메시지 전송을 위한 DTO
 */
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class NotificationMessage {

    private Long userId;     // 알림을 받을 사용자 ID
    private String type;     // 알림 유형
    private String message;  // 알림 메시지
    private Long targetId;   // 북마크 ID or 공유컬렉션 ID
}
