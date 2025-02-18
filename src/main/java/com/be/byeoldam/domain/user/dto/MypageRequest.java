package com.be.byeoldam.domain.user.dto;

import com.be.byeoldam.domain.bookmark.dto.TagDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MypageRequest {
    String nickname;
    int alertDay;
    List<TagDto> tagList;
}
