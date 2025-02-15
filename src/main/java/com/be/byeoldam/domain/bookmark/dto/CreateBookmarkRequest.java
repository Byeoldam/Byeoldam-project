package com.be.byeoldam.domain.bookmark.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateBookmarkRequest {
    private String url;
    private Long collectionId;

    @Valid
    private List<TagDto> tags;

    @JsonProperty("isPersonal")
    private boolean personal;

    private int readingTime;
}
