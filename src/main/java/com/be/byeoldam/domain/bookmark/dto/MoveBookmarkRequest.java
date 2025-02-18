package com.be.byeoldam.domain.bookmark.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MoveBookmarkRequest {
    private Long collectionId;

    @JsonProperty("isPersonal")
    private boolean isPersonal;
}
