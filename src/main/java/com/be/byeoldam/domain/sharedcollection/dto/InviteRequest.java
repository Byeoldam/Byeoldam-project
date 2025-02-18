package com.be.byeoldam.domain.sharedcollection.dto;

import com.be.byeoldam.domain.notification.model.InviteNotification;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InviteRequest {

    @NotBlank
    private String email;

}
