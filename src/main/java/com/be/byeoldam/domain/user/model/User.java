package com.be.byeoldam.domain.user.model;

import com.be.byeoldam.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user")
public class User extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 60, nullable = false, columnDefinition = "ENUM('LOCAL', 'KAKAO', 'NAVER', 'GOOGLE') DEFAULT 'LOCAL'")
    private Provider provider;

    @Column(name = "provider_id", length = 60, nullable = true)
    private String providerId;

    @Column(length = 256, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String role;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(name = "alert_day", nullable = false)
    private Integer alertDay;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "email_verification_token", length = 255, nullable = true)
    private String emailVerificationToken;

    // 이메일 인증
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(name = "access_token", length = 255, nullable = true)
    private String accessToken;

    @Column(name = "refresh_token", length = 255, nullable = true)
    private String refreshToken;

    // 계정 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "is_active", nullable = false, columnDefinition = "ENUM('PENDING', 'ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'PENDING'")
    private AccountStatus isActive;

    @Column(name = "profile_url", length = 500, nullable = false)
    private String profileUrl;

    // 비밀번호 암호화 메서드 추가
    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public void updateProfileImage(String profileUrl){
        this.profileUrl = profileUrl;
    }

    public void updateMypage(String nickname, int alertDay) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (alertDay != 0) {
            this.alertDay = alertDay;
        }
    }

    public void deactivate(){
        this.deletedAt = LocalDateTime.now();
        this.isActive = AccountStatus.INACTIVE;
    }

    @Builder
    public User(String email, String password, String nickname, Provider provider, Integer alertDay, Boolean isVerified, AccountStatus isActive, String profileUrl, String providerId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.alertDay = alertDay;
        this.isVerified = isVerified;
        this.isActive = isActive;
        this.profileUrl = profileUrl;
        this.providerId = providerId;
    }


    @PrePersist
    private void prePersist() {
        if (this.provider == null) {
            this.provider = Provider.LOCAL;
        }
        if (this.alertDay == null) {
            this.alertDay = 7;
        }
        if (this.isVerified == null) {
            this.isVerified = true;
        }
        if (this.isActive == null) {
            this.isActive = AccountStatus.ACTIVE;
        }
        if(this.profileUrl == null) {
            profileUrl ="https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png";
        }
        if(this.role == null){
            role = "ROLE_USER";
        }
    }

    public enum AccountStatus {
        PENDING,   // 인증 대기 중
        ACTIVE,    // 인증 완료 및 활성화
        INACTIVE,  // 비활성화된 계정
        LOCKED     // 잠긴 계정
    }
    public enum Provider {
        LOCAL, KAKAO, NAVER, GOOGLE
    }
}
