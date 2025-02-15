package com.be.byeoldam.domain.personalcollection.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PersonalBookmarkListResponse {

    private Long collectionId;
    private List<PersonalBookmarkResponse> bookmarks;

    public static PersonalBookmarkListResponse of(Long collectionId, List<PersonalBookmarkResponse> bookmarks) {
        return PersonalBookmarkListResponse.builder()
                .collectionId(collectionId)
                .bookmarks(bookmarks)
                .build();
    }

}
