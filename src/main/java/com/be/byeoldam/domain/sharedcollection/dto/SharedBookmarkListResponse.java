package com.be.byeoldam.domain.sharedcollection.dto;

import com.be.byeoldam.domain.personalcollection.dto.PersonalBookmarkResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SharedBookmarkListResponse {

    private Long collectionId;
    private List<SharedBookmarkResponse> bookmarks;

    public static SharedBookmarkListResponse of(Long collectionId, List<SharedBookmarkResponse> bookmarks) {
        return SharedBookmarkListResponse.builder()
                .collectionId(collectionId)
                .bookmarks(bookmarks)
                .build();
    }

}
