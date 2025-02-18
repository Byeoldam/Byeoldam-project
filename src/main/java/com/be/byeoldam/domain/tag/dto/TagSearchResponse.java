package com.be.byeoldam.domain.tag.dto;

import com.be.byeoldam.domain.personalcollection.dto.PersonalBookmarkResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TagSearchResponse {
    private List<PersonalBookmarkResponse> personalBookmarkList;
    private List<RecommendedUrlResponse> recommendedUrlList;

    public TagSearchResponse() {
        personalBookmarkList = new ArrayList<>();
        recommendedUrlList = new ArrayList<>();
    }

    private TagSearchResponse(List<PersonalBookmarkResponse> personalBookmarkList, List<RecommendedUrlResponse> recommendedUrlList) {
        this.personalBookmarkList = personalBookmarkList;
        this.recommendedUrlList = recommendedUrlList;
    }

    public static TagSearchResponse of(List<PersonalBookmarkResponse> personalBookmarkList, List<RecommendedUrlResponse> recommendedUrlList) {
        return new TagSearchResponse(personalBookmarkList, recommendedUrlList);
    }
}
