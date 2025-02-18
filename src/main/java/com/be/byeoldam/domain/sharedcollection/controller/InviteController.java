package com.be.byeoldam.domain.sharedcollection.controller;


import com.be.byeoldam.common.ResponseTemplate;
import com.be.byeoldam.common.annotation.UserId;
import com.be.byeoldam.domain.sharedcollection.dto.InviteRequest;
import com.be.byeoldam.domain.sharedcollection.service.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Invite", description = "공유컬렉션 초대 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collections/shared")
public class InviteController {

    private final InviteService inviteService;


    @Operation(summary = "공유 컬렉션 초대", description = "공유 컬렉션에 다른 사용자를 초대합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "초대 성공"
    )
    @PostMapping("/{collectionId}/invite")
    public ResponseTemplate<Void> inviteUser(
            @PathVariable Long collectionId,
            @UserId Long inviterId,
            @Valid @RequestBody InviteRequest request
    ) {
        inviteService.inviteUser(inviterId, collectionId, request);
        return ResponseTemplate.ok();
    }
}
