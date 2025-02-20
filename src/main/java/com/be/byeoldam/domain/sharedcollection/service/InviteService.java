package com.be.byeoldam.domain.sharedcollection.service;

import com.be.byeoldam.domain.notification.NotificationRepository;
import com.be.byeoldam.domain.notification.dto.NotificationMessage;
import com.be.byeoldam.domain.notification.event.NotificationProducer;
import com.be.byeoldam.domain.notification.model.InviteNotification;
import com.be.byeoldam.domain.sharedcollection.dto.InviteRequest;
import com.be.byeoldam.domain.sharedcollection.model.SharedCollection;
import com.be.byeoldam.domain.sharedcollection.repository.SharedCollectionRepository;
import com.be.byeoldam.domain.sharedcollection.repository.SharedUserRepository;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final SharedCollectionRepository sharedCollectionRepository;
    private final SharedUserRepository sharedUserRepository;
    private final UserRepository userRepository;
    private final NotificationProducer notificationProducer;

    @Transactional
    public void inviteUser(Long inviterId, Long collectionId, InviteRequest request) {
        SharedCollection sharedCollection = sharedCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException("공유 컬렉션을 찾을 수 없습니다."));

        // 초대 받는 사람이 존재하는지 확인
        User invitee = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("초대할 사용자를 찾을 수 없습니다."));

        // 초대 하는 사람
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new CustomException("초대하는 사용자를 찾을 수 없습니다."));

        // 공유 컬렉션에 있는 사용자인지 확인
        boolean isSharedUser = sharedUserRepository.existsBySharedCollectionIdAndUserId(collectionId, inviterId);
        if (!isSharedUser) {
            throw new CustomException("공유 컬렉션의 멤버만 초대할 수 있습니다.");
        }

        // 이미 멤버인지 확인
        boolean isAlreadyMember = sharedUserRepository.existsBySharedCollectionIdAndUserId(collectionId, invitee.getId());
        if (isAlreadyMember) {
            throw new CustomException("이미 멤버인 사용자입니다.");
        }

        NotificationMessage message = new NotificationMessage(
                invitee.getId(),
                "INVITE",
                inviter.getNickname() + "님이 공유 컬렉션에 초대하였습니다.",
                collectionId
        );

        notificationProducer.sendNotification(message);

    }

}
