package com.be.byeoldam.common.filter;

import com.be.byeoldam.domain.user.model.User;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@ToString
public class CustomUserDetails implements UserDetails {
    private long userId;
    private String email;
    private String password;
    private String nickname;
    private String profileUrl;
    private CustomUserDetails(long userId, String email, String password, String nickname, String profileUrl) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

    public static CustomUserDetails fromEntity(User user) {
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), user.getNickname(), user.getProfileUrl());
    }
    public String getProfileUrl() {
        return profileUrl;
    }

    public String getEmail() {
        return email;
    }

    public long getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
