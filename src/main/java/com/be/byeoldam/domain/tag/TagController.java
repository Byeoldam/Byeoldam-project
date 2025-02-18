package com.be.byeoldam.domain.tag;

import com.be.byeoldam.common.ResponseTemplate;
import com.be.byeoldam.common.annotation.UserId;
import com.be.byeoldam.domain.tag.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@Tag(name = "Tag", description = "Tag 관련 API")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(
            summary = "추천 태그",
            description = "북마크가 가장 많이 들고 있는 상위 10개의 태그를 조회"
    )
    @GetMapping("/recommend")
    public ResponseTemplate<RecommendedTagResponse> recommendTags(){
        RecommendedTagResponse response = tagService.getRecommendedTags();
        return ResponseTemplate.ok(response);
    }


    @Operation(
            summary = "사용자 관심 태그 조회",
            description = "사용자가 저장한 관심 태그를 조회합니다. 해당 API를 사용할 곳이 있는지 미지수ㅎㅎ"
    )
    @GetMapping
    public ResponseTemplate<UserTagResponse> getUserTag(@UserId long userId){
        UserTagResponse response = tagService.getUserTags(userId);
        return ResponseTemplate.ok(response);
    }


    @Operation(
            summary = "사용자 관심 태그 저장",
            description = "사용자가 선택한 관심 태그를 저장합니다. 커스텀 페이지2에서 사용."
    )
    @PostMapping
    public ResponseTemplate<UserTagResponse> addUserTag(@UserId long userId, @RequestBody UserTagRequest request){
        UserTagResponse response = tagService.addUserTag(userId, request.getTagList());
        return ResponseTemplate.ok(response);
    }


    /*
    조건 0. 로그인을 하지 않은 경우,태그를 입력하지 않은 경우는 없음
    조건 1. UserId와 tag는 필수로 넘어옴.
    조건 2. 공유 컬렉션의 북마크는 가져오지 않는다.
    */
    @Operation(
            summary = "태그 기반 북마크 검색",
            description = """
            특정 태그를 기준으로 사용자 북마크를 검색합니다.
            조건:
            1. 로그인을 하지 않은 경우 호출할 수 없습니다.
            2. UserId와 tag는 필수로 제공되어야 합니다.
            3. 공유 컬렉션의 북마크는 검색 결과에 포함되지 않습니다.
            페이징 처리를 위해 cursorId와 size를 사용하며, 기본값은 각각 0과 6입니다.
        """
    )
    @GetMapping("/search")
    public ResponseTemplate<TagSearchResponse> searchTag(
            @UserId long userId,
            @RequestParam(value = "cursorId", required = true, defaultValue = "0") int cursorId,
            @RequestParam(value = "size", required = true, defaultValue = "6") int size,
            @RequestParam(value = "tag", required = true) String tagName
    ){
        TagSearchResponse response = tagService.searchTag(userId, tagName, cursorId, size);
        return ResponseTemplate.ok(response);
    }


    // 태그 기반 URL 추천. (태그 이름으로 다른 유저들이 많이한 북마크에서 Url을 가져와 추천)
    // tagName 입력값으로 주지 않을 시, 가장 인기 있는 Url들을 추천해줌.
    @Operation(
            summary = "태그 기반 URL 추천",
            description = """
            특정 태그를 기준으로 다른 사용자들이 북마크한 URL을 추천합니다.
            입력값:
            - cursorId: 페이징 시작 위치 (기본값: 0)
            - size: 반환할 URL 개수 (기본값: 10)
            - tag: 검색할 태그 이름 (필수가 아니며, 태그 이름이 없을 경우 가장 인기 있는 URL들을 추천합니다.)
            결과는 다른 사용자들이 많이 북마크한 URL을 기반으로 반환됩니다.
        """
    )
    @GetMapping("/recommend-search")
    public ResponseTemplate<List<RecommendedUrlResponse>> getRecommendedUrlsByTag(
            @RequestParam(value = "cursorId", required = false, defaultValue = "0") int cursorId,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "tag", required = false) String tagName
    ){
        List<RecommendedUrlResponse> responses = tagService.getBookmarkUrlsByTagName(tagName, cursorId, size);
        return ResponseTemplate.ok(responses);
    }

}
