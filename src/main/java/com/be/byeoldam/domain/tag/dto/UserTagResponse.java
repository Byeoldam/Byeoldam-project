package com.be.byeoldam.domain.tag.dto;

import com.be.byeoldam.domain.bookmark.dto.TagDto;
import com.be.byeoldam.domain.tag.model.UserTag;
import lombok.Getter;

import java.util.List;

@Getter
public class UserTagResponse {
    List<TagDto> tagList;

    private UserTagResponse(List<TagDto> tagList){
        this.tagList = tagList;
    }

    public static UserTagResponse of(List<TagDto> tagList){
        return new UserTagResponse(tagList);
    }
}
