package com.be.byeoldam.domain.user.dto;

import com.be.byeoldam.domain.bookmark.dto.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class MypageResponse {
    String email;
    String nickname;
    String profileUrl;
    int alrerDay;
    List<TagDto> tagList;

}
