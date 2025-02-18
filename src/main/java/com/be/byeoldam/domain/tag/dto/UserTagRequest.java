package com.be.byeoldam.domain.tag.dto;

import com.be.byeoldam.domain.bookmark.dto.TagDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class UserTagRequest {
    List<TagDto> tagList;
}
