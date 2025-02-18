package com.be.byeoldam.domain.tag.dto;

import com.be.byeoldam.domain.bookmark.dto.TagDto;
import lombok.Getter;

import java.util.List;

@Getter
public class RecommendedTagResponse {
    List<TagDto> tagList;

    private RecommendedTagResponse(List<TagDto> tagList){
        this.tagList = tagList;
    }

    public static RecommendedTagResponse of(List<TagDto> tagList){
        return new RecommendedTagResponse(tagList);
    }
}
