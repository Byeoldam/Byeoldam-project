package com.be.byeoldam.domain.personalcollection;

import com.be.byeoldam.common.ResponseTemplate;
import com.be.byeoldam.common.annotation.UserId;
import com.be.byeoldam.domain.personalcollection.dto.PersonalBookmarkResponse;
import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionRequest;
import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "PersonalCollection", description = "개인컬렉션")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collections/personal")
public class PersonalCollectionController {

    private final PersonalCollectionService personalCollectionService;

    @Operation(summary = "개인컬렉션 생성", description = "개인컬렉션 생성")
    @ApiResponse(responseCode = "200", description = "개인컬렉션 생성 성공", useReturnTypeSchema = true)
    @PostMapping
    public ResponseTemplate<Void> createPersonalCollection(
            @Valid @RequestBody PersonalCollectionRequest request,
            @UserId Long userId
    ) {

        personalCollectionService.createPersonalCollection(request, userId);
        return ResponseTemplate.ok();
    }

    @Operation(summary = "개인컬렉션 목록 조회", description = "개인컬렉션 목록 조회")
    @ApiResponse(responseCode = "200", description = "개인컬렉션 목록 조회 성공", useReturnTypeSchema = true)
    @GetMapping
    public ResponseTemplate<List<PersonalCollectionResponse>> getPersonalCollection(
            @UserId Long userId
    ) {

        return ResponseTemplate.ok(personalCollectionService.getPersonalCollections(userId));

    }

    // 개인컬렉션 이름 수정
    @Operation(summary = "개인컬렉션 수정", description = "개인컬렉션 이름 수정")
    @ApiResponse(responseCode = "200", description = "", useReturnTypeSchema = true)
    @PutMapping("/{personalCollectionId}")
    public ResponseTemplate<PersonalCollectionResponse> updatePeronalCollection(
            @Valid @RequestBody PersonalCollectionRequest request,
            @UserId Long userId,
            @PathVariable Long personalCollectionId
    ) {

        return ResponseTemplate.ok(personalCollectionService.updatePersonalCollection(request, userId, personalCollectionId));
    }

    @Operation(summary = "개인컬렉션 삭제", description = "개인컬렉션 삭제")
    @ApiResponse(responseCode = "200", description = "개인컬렉션 멤버 조회 성공", useReturnTypeSchema = true)
    @DeleteMapping("/{personalCollectionId}")
    public ResponseTemplate<Void> deleteSharedCollection(
            @UserId Long userId,
            @PathVariable Long personalCollectionId
    ) {

        personalCollectionService.deletePersonalCollection(userId, personalCollectionId);
        return ResponseTemplate.ok();
    }

    @Operation(summary = "개인컬렉션 북마크 목록 조회", description = "개인컬렉션의 북마크 목록 조회")
    @ApiResponse(responseCode = "200", description = "개인컬렉션 북마크 목록 조회 성공", useReturnTypeSchema = true)
    @GetMapping("/{personalCollectionId}")
    public ResponseTemplate<List<PersonalBookmarkResponse>> getPersonalBookmark(
            @UserId Long userId,
            @PathVariable Long personalCollectionId

    ) {

        return ResponseTemplate.ok(personalCollectionService.getCollectionBookmark(userId, personalCollectionId));
    }

    @Operation(summary = "오래된 북마크 조회", description = "30일 이상 보지 않은 북마크 목록 조회")
    @ApiResponse(responseCode = "200", description = "오래된 북마크 목록 조회 성공", useReturnTypeSchema = true)
    @GetMapping("/long-unread")
    public ResponseTemplate<List<PersonalBookmarkResponse>> getLongUnreadBookmark(
            @UserId Long userId
    ) {

        return ResponseTemplate.ok(personalCollectionService.getLongUnreadBookmark(userId));
    }

    @Operation(summary = "중요 북마크 조회", description = "중요도 설정 북마크 조회")
    @ApiResponse(responseCode = "200", description = "중요 북마크 목록 조회 성공", useReturnTypeSchema = true)
    @GetMapping("/priority")
    public ResponseTemplate<List<PersonalBookmarkResponse>> getPersonalBookmarkPriority(
            @UserId Long userId
    ) {

        return ResponseTemplate.ok(personalCollectionService.getPriorityBookmark(userId));
    }
}
