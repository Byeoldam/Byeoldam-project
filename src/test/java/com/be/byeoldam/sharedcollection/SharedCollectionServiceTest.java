package com.be.byeoldam.sharedcollection;

import com.be.byeoldam.domain.sharedcollection.service.SharedCollectionService;
import com.be.byeoldam.domain.sharedcollection.dto.SharedCollectionRequest;
import com.be.byeoldam.domain.sharedcollection.dto.SharedCollectionResponse;
import com.be.byeoldam.domain.sharedcollection.model.Role;
import com.be.byeoldam.domain.sharedcollection.model.SharedCollection;
import com.be.byeoldam.domain.sharedcollection.model.SharedUser;
import com.be.byeoldam.domain.sharedcollection.repository.SharedCollectionRepository;
import com.be.byeoldam.domain.sharedcollection.repository.SharedUserRepository;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SharedCollectionServiceTest {

    @InjectMocks
    private SharedCollectionService sharedCollectionService;

    @Mock
    private SharedCollectionRepository sharedCollectionRepository;

    @Mock
    private SharedUserRepository sharedUserRepository;

    @Mock
    private UserRepository userRepository;

    private User ownerUser;
    private User invitedUser;
    private SharedCollection sharedCollection;
    private SharedUser ownerSharedUser;

    @BeforeEach
    void setUp() {
        ownerUser = User.builder()
                .email("owner@example.com")
                .password("password123")
                .nickname("ownerUser")
                .provider(User.Provider.LOCAL)
                .alertDay(7)
                .isVerified(true)
                .isActive(User.AccountStatus.ACTIVE)
                .profileUrl("default.png")
                .build();
        ReflectionTestUtils.setField(ownerUser, "id", 1L);

        invitedUser = User.builder()
                .email("invited@example.com")
                .password("password123")
                .nickname("invitedUser")
                .provider(User.Provider.LOCAL)
                .alertDay(7)
                .isVerified(true)
                .isActive(User.AccountStatus.ACTIVE)
                .profileUrl("default.png")
                .build();
        ReflectionTestUtils.setField(invitedUser, "id", 2L);

        sharedCollection = SharedCollection.create("sharedCollection");
        ReflectionTestUtils.setField(sharedCollection, "id", 1L);

        ownerSharedUser = SharedUser.create(ownerUser, sharedCollection, Role.OWNER);
        ReflectionTestUtils.setField(ownerSharedUser, "id", 1L);
    }

    @Test
    void createSharedCollectionTest_Success() {
        // given
        SharedCollectionRequest request = new SharedCollectionRequest("sharedCollection");

        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
        when(sharedCollectionRepository.save(any(SharedCollection.class))).thenReturn(sharedCollection);
        when(sharedUserRepository.save(any(SharedUser.class))).thenReturn(ownerSharedUser);

        // when
        sharedCollectionService.createSharedCollection(request, 1L);

        // then
        verify(sharedCollectionRepository, times(1)).save(any(SharedCollection.class));
        verify(sharedUserRepository, times(1)).save(any(SharedUser.class));
    }

    @Test
    void createSharedCollectionTest_Fail_UserNotFound() {
        // given
        SharedCollectionRequest request = new SharedCollectionRequest("sharedCollection");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sharedCollectionService.createSharedCollection(request, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    void getSharedCollectionTest_Success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
        when(sharedUserRepository.findByUser(ownerUser)).thenReturn(List.of(ownerSharedUser));

        // when
        List<SharedCollectionResponse> responses = sharedCollectionService.getSharedCollection(1L);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCollectionId()).isEqualTo(1L);
        assertThat(responses.get(0).getName()).isEqualTo("sharedCollection");
    }

    @Test
    void updateSharedCollectionTest_Success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.of(sharedCollection));
        when(sharedUserRepository.findByUserAndSharedCollection(ownerUser, sharedCollection)).thenReturn(Optional.of(ownerSharedUser));

        // when
        SharedCollectionResponse response =
                sharedCollectionService.updateSharedCollection(new SharedCollectionRequest("Updated Name"), 1L, 1L);

        // then
        assertThat(response.getCollectionId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Updated Name");
    }

    @Test
    void updateSharedCollectionTest_Fail_NotOwner() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.of(sharedCollection));
        when(sharedUserRepository.findByUserAndSharedCollection(ownerUser, sharedCollection))
                .thenReturn(Optional.of(SharedUser.create(ownerUser, sharedCollection, Role.MEMBER))); // OWNER가 아님

        // when & then
        assertThatThrownBy(() -> sharedCollectionService.updateSharedCollection(new SharedCollectionRequest("Updated Name"), 1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 권한은 방장만 가능합니다.");
    }

//    @Test
//    void deleteSharedCollectionTest_Success() {
//        // given
//        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
//        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.of(sharedCollection));
//        when(sharedUserRepository.findByUserAndSharedCollection(ownerUser, sharedCollection)).thenReturn(Optional.of(ownerSharedUser));
//
//        // when
//        sharedCollectionService.deleteSharedCollection(1L, 1L);
//
//        // then
//        verify(sharedCollectionRepository, times(1)).delete(sharedCollection);
//        verify(sharedUserRepository, times(1)).deleteAllBySharedCollection(sharedCollection);
//    }

    @Test
    void deleteSharedCollectionTest_Fail_NotOwner() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.of(sharedCollection));
        when(sharedUserRepository.findByUserAndSharedCollection(ownerUser, sharedCollection))
                .thenReturn(Optional.of(SharedUser.create(ownerUser, sharedCollection, Role.MEMBER))); // OWNER가 아님

        // when & then
        assertThatThrownBy(() -> sharedCollectionService.deleteSharedCollection(1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 권한은 방장만 가능합니다.");
    }

//    @Test
//    void inviteNewMemberTest_Success() {
//        // given
//        when(userRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
//        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.of(sharedCollection));
//        when(sharedUserRepository.findByUserAndSharedCollection(invitedUser, sharedCollection)).thenReturn(Optional.empty());
//
//        // when
//        sharedCollectionService.inviteNewMember(2L, 1L);
//
//        // then
//        verify(sharedUserRepository, times(1)).save(any(SharedUser.class));
//    }

//    @Test
//    void inviteNewMemberTest_Fail_AlreadyMember() {
//        // given
//        when(userRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
//        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.of(sharedCollection));
//        when(sharedUserRepository.findByUserAndSharedCollection(invitedUser, sharedCollection))
//                .thenReturn(Optional.of(SharedUser.create(invitedUser, sharedCollection, Role.MEMBER)));
//
//        // when & then
//        assertThatThrownBy(() -> sharedCollectionService.inviteNewMember(2L, 1L))
//                .isInstanceOf(CustomException.class)
//                .hasMessage("이미 초대된 유저입니다.");
//    }

    @Test
    void ejectMemberTest_Success() {
        // given
        SharedUser memberUser = SharedUser.create(invitedUser, sharedCollection, Role.MEMBER);
        ReflectionTestUtils.setField(memberUser, "id", 2L);

        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.of(sharedCollection));
        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
        when(sharedUserRepository.findByUserAndSharedCollection(ownerUser, sharedCollection))
                .thenReturn(Optional.of(ownerSharedUser));
        when(sharedUserRepository.findByUserAndSharedCollection(invitedUser, sharedCollection))
                .thenReturn(Optional.of(memberUser));

        // when
        sharedCollectionService.ejectMember(1L, 1L, 2L);

        // then
        verify(sharedUserRepository, times(1)).delete(memberUser);
    }

    @Test
    void ejectMemberTest_Fail_NotOwner() {
        // given
        SharedUser memberUser = SharedUser.create(invitedUser, sharedCollection, Role.MEMBER);

        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.of(sharedCollection));
        when(userRepository.findById(1L)).thenReturn(Optional.of(invitedUser)); // OWNER가 아님
        when(userRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
        when(sharedUserRepository.findByUserAndSharedCollection(invitedUser, sharedCollection))
                .thenReturn(Optional.of(memberUser));

        // when & then
        assertThatThrownBy(() -> sharedCollectionService.ejectMember(1L, 1L, 2L))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 권한은 방장만 가능합니다.");
    }

    @Test
    void ejectMemberTest_Fail_NotMember() {
        // given
        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.of(sharedCollection));
        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
        when(sharedUserRepository.findByUserAndSharedCollection(ownerUser, sharedCollection))
                .thenReturn(Optional.of(ownerSharedUser));
        when(sharedUserRepository.findByUserAndSharedCollection(invitedUser, sharedCollection))
                .thenReturn(Optional.empty()); // 강퇴 대상이 컬렉션에 없음

        // when & then
        assertThatThrownBy(() -> sharedCollectionService.ejectMember(1L, 1L, 2L))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 컬렉션의 멤버가 아닙니다.");
    }

    @Test
    void deleteSharedCollectionTest_Fail_AlreadyDeleted() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.empty()); // 컬렉션이 이미 삭제됨

        // when & then
        assertThatThrownBy(() -> sharedCollectionService.deleteSharedCollection(1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("컬렉션을 찾을 수 없습니다.");
    }

//    @Test
//    void inviteNewMemberTest_Fail_AlreadyOwner() {
//        // given
//        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerUser));
//        when(sharedCollectionRepository.findById(1L)).thenReturn(Optional.of(sharedCollection));
//        when(sharedUserRepository.findByUserAndSharedCollection(ownerUser, sharedCollection))
//                .thenReturn(Optional.of(ownerSharedUser)); // 초대하려는 사용자가 이미 방장
//
//        // when & then
//        assertThatThrownBy(() -> sharedCollectionService.inviteNewMember(1L, 1L))
//                .isInstanceOf(CustomException.class)
//                .hasMessage("이미 초대된 유저입니다.");
//    }
}
