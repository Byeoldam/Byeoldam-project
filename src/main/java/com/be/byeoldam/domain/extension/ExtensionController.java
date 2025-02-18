package com.be.byeoldam.domain.extension;

import com.be.byeoldam.common.ResponseTemplate;
import com.be.byeoldam.common.annotation.UserId;
import com.be.byeoldam.domain.extension.dto.ExtensionDataResponse;
import com.be.byeoldam.domain.extension.dto.ExtensionRequest;
import com.be.byeoldam.domain.extension.service.ExtensionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Extension", description = "Extension API")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "chrome-extension://djiemmdlekojnbiclbgdepjapkoejddm")
@RequestMapping("/api/popup/load")
public class ExtensionController {

    private final ExtensionService extensionService;

    @Operation(
            summary = "팝업창 초기 데이터 조회",
            description = "사용자의 컬렉션 목록, 페이지 키워드, RSS 구독 여부, 알림 개수, 새로운 피드 존재 여부를 조회합니다.")
    @PostMapping
    public ResponseTemplate<ExtensionDataResponse> getExtensionData(
            @UserId long userId,
            @RequestBody ExtensionRequest request
    ) {
        if (request.getSiteUrl() == null) {
            return ResponseTemplate.fail("siteUrl 값이 비어있습니다.");
        }

        if (request.getTitle() == null) {
            return ResponseTemplate.fail("title 값이 비어있습니다.");
        }

        return ResponseTemplate.ok(extensionService.getLoadList(userId, request));
    }
}
