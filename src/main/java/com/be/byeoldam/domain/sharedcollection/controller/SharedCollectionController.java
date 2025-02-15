package com.be.byeoldam.domain.sharedcollection.controller;

import com.be.byeoldam.common.ResponseTemplate;
import com.be.byeoldam.common.annotation.UserId;
import com.be.byeoldam.domain.sharedcollection.dto.*;
import com.be.byeoldam.domain.sharedcollection.service.SharedCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SharedCollection", description = "공유컬렉션")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collections/shared")
public class SharedCollectionController {

    private final SharedCollectionService sharedCollectionService;

    @Operation(summary = "공유컬렉션 생성", description = "공우컬렉션 생성")
    @ApiResponse(responseCode = "200", description = "공유컬렉션 생성 성공", useReturnTypeSchema = true)
    @PostMapping
    public ResponseTemplate<Void> createSharedCollection(
            @Valid @RequestBody SharedCollectionRequest request,
            @UserId Long userId
    ) {

        sharedCollectionService.createSharedCollection(request, userId);
        return ResponseTemplate.ok();
    }

    @Operation(summary = "공유컬렉션 목록 조회", description = "공유컬렉션 목록 조회")
    @ApiResponse(responseCode = "200", description = "공유컬렉션 목록 조회 성공", useReturnTypeSchema = true)
    @GetMapping
    public ResponseTemplate<List<SharedCollectionResponse>> getSharedCollection(
            @UserId Long userId
    ) {

        return ResponseTemplate.ok(sharedCollectionService.getSharedCollection(userId));

    }

    // 공유컬렉션 이름 수정
    @Operation(summary = "공유컬렉션 수정", description = "공유컬렉션 이름 수정")
    @ApiResponse(responseCode = "200", description = "공유컬렉션 수정 성공", useReturnTypeSchema = true)
    @PutMapping("/{sharedCollectionId}")
    public ResponseTemplate<SharedCollectionResponse> updateSharedCollection(
            @Valid @RequestBody SharedCollectionRequest request,
            @UserId Long userId,
            @PathVariable Long sharedCollectionId
    ) {

        return ResponseTemplate.ok(sharedCollectionService.updateSharedCollection(request, userId, sharedCollectionId));
    }

    @Operation(summary = "공유컬렉션 북마크 조회", description = "공유컬렉션의 북마크 조회")
    @ApiResponse(responseCode = "200", description = "공유컬렉션 북마크 조회 성공", useReturnTypeSchema = true)
    @GetMapping("/{sharedCollectionId}")
    public ResponseTemplate<SharedBookmarkListResponse> getSharedBookmark(
            @PathVariable Long sharedCollectionId,
            @UserId Long userId
    ) {

        return ResponseTemplate.ok(sharedCollectionService.getCollectionBookmark(userId, sharedCollectionId));
    }

    @Operation(summary = "공유컬렉션 멤버 조회", description = "공유컬렉션 내 멤버 조회")
    @ApiResponse(responseCode = "200", description = "공유컬렉션 멤버 조회 성공", useReturnTypeSchema = true)
    @GetMapping("/{sharedCollectionId}/users")
    public ResponseTemplate<List<CollectionMemberResponse>> getMember(
            @PathVariable Long sharedCollectionId,
            @UserId Long userId
    ) {

        return ResponseTemplate.ok(sharedCollectionService.getMember(userId, sharedCollectionId));
    }

    @Operation(summary = "공유컬렉션 삭제", description = "공유컬렉션 삭제")
    @ApiResponse(responseCode = "200", description = "공유컬렉션 멤버 조회 성공", useReturnTypeSchema = true)
    @DeleteMapping("/{sharedCollectionId}")
    public ResponseTemplate<Void> deleteSharedCollection(
            @UserId Long userId,
            @PathVariable Long sharedCollectionId
    ) {

        sharedCollectionService.deleteSharedCollection(userId, sharedCollectionId);
        return ResponseTemplate.ok();
    }

    @Operation(summary = "공유컬렉션 초대 수락", description = "공유컬렉션 초대 알림을 수락합니다.")
    @ApiResponse(responseCode = "200", description = "공유컬렉션 수락 성공", useReturnTypeSchema = true)
    @PostMapping("/invite")
    public ResponseTemplate<Void> acceptSharedCollection(
            @Valid @RequestBody InviteAcceptRequest request,
            @UserId Long userId
    ) {
        sharedCollectionService.inviteNewMember(request, userId);
        return ResponseTemplate.ok();
    }

    @Operation(summary = "공유컬렉션 멤버 강퇴", description = "공유컬렉션의 멤버를 강퇴")
    @ApiResponse(responseCode = "200", description = "공유컬렉션 멤버 강퇴", useReturnTypeSchema = true)
    @DeleteMapping("/{sharedCollectionId}/members/{ejectedUserId}")
    public ResponseTemplate<Void> ejectMember(
            @UserId Long userId,
            @PathVariable Long sharedCollectionId,
            @PathVariable Long ejectedUserId
    ) {

        sharedCollectionService.ejectMember(userId, sharedCollectionId, ejectedUserId);
        return ResponseTemplate.ok();
    }
}