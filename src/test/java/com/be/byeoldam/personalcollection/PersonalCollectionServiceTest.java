package com.be.byeoldam.personalcollection;

import com.be.byeoldam.domain.personalcollection.PersonalCollectionService;
import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionRequest;
import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionResponse;
import com.be.byeoldam.domain.personalcollection.model.PersonalCollection;
import com.be.byeoldam.domain.personalcollection.repository.PersonalCollectionRepository;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.model.User.Provider;
import com.be.byeoldam.domain.user.model.User.AccountStatus;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonalCollectionServiceTest {

    @InjectMocks
    private PersonalCollectionService personalCollectionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PersonalCollectionRepository personalCollectionRepository;

    private User mockUser;
    private PersonalCollection mockCollection;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("testUser")
                .provider(Provider.LOCAL)
                .alertDay(7)
                .isVerified(true)
                .isActive(AccountStatus.ACTIVE)
                .profileUrl("default.png")
                .build();

        ReflectionTestUtils.setField(mockUser, "id", 1L);

        mockCollection = PersonalCollection.create("ecollection", mockUser);
        ReflectionTestUtils.setField(mockCollection, "id", 1L);
    }

    @Test
    void createPersonalCollectionTest_Success() {
        // given
        PersonalCollectionRequest request = new PersonalCollectionRequest("ecollection");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(personalCollectionRepository.existsByUserIdAndName(1L, "ecollection")).thenReturn(false);
        when(personalCollectionRepository.save(any(PersonalCollection.class))).thenReturn(mockCollection);

        // when
        personalCollectionService.createPersonalCollection(request, 1L);

        // then
        verify(personalCollectionRepository, times(1)).save(any(PersonalCollection.class));
    }

    @Test
    void createPersonalCollection_Fail_DuplicatedName() {
        // given
        PersonalCollectionRequest request = new PersonalCollectionRequest("ecollection");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(personalCollectionRepository.existsByUserIdAndName(anyLong(), eq("ecollection"))).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> personalCollectionService.createPersonalCollection(request, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("같은 이름의 컬렉션이 이미 존재합니다.");
    }



    @Test
    void updatePersonalCollection_Success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(personalCollectionRepository.findById(1L)).thenReturn(Optional.of(mockCollection));
        when(personalCollectionRepository.existsByUserIdAndName(anyLong(), eq("Updated Name"))).thenReturn(false);

        // when
        PersonalCollectionResponse response =
                personalCollectionService.updatePersonalCollection(new PersonalCollectionRequest("Updated Name"), 1L, 1L);

        // then
        assertThat(response.getCollectionId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Updated Name");
    }

    @Test
    void updatePersonalCollection_Fail_DuplicatedName() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(personalCollectionRepository.findById(1L)).thenReturn(Optional.of(mockCollection));
        when(personalCollectionRepository.existsByUserIdAndName(1L, "Updated Name")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> personalCollectionService.updatePersonalCollection(new PersonalCollectionRequest("Updated Name"), 1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("같은 이름의 컬렉션이 이미 존재합니다.");
    }




//    @Test
//    void deletePersonalCollection_Success() {
//        // given
//        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
//        when(personalCollectionRepository.findById(1L)).thenReturn(Optional.of(mockCollection));
//
//        // when
//        personalCollectionService.deletePersonalCollection(1L, 1L);
//
//        // then
//        verify(personalCollectionRepository, times(1)).delete(mockCollection);
//    }

    @Test
    void deletePersonalCollection_Fail_NotFound() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(personalCollectionRepository.findById(999L)).thenReturn(Optional.empty()); // ❌ 존재하지 않는 컬렉션

        // when & then
        assertThatThrownBy(() -> personalCollectionService.deletePersonalCollection(1L, 999L))
                .isInstanceOf(CustomException.class)
                .hasMessage("컬렉션을 찾을 수 없습니다.");
    }
}
