package com.be.byeoldam.domain.bookmark.dto;

import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionRequest;
import com.be.byeoldam.domain.sharedcollection.dto.SharedCollectionRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateBookmarkAndCollectionRequest {

    private String url;

    private boolean personal;

    private int readingTime;

    @NotBlank
    @Size(max = 20, message = "컬렉션의 이름은 20자까지 가능합니다.")
    private String collectionName;

    @Valid
    private List<TagDto> tags;

    public CreateBookmarkRequest makeBookmarkRequest(Long collectionId) {
        return new CreateBookmarkRequest(this.url, collectionId, this.tags, this.personal, this.readingTime);
    }

    public PersonalCollectionRequest makePersonalCollection() {
        return new PersonalCollectionRequest(this.collectionName);
    }

    public SharedCollectionRequest makeSharedCollectionRequest() {
        return new SharedCollectionRequest(this.getCollectionName());
    }
}