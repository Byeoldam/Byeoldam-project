package com.be.byeoldam.domain.sharedcollection.dto;

import com.be.byeoldam.domain.sharedcollection.model.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CollectionMemberResponse {

    private Long userId;
    private String email;
    private String nickname;
    private String profileUrl;
    private Role role;
}
