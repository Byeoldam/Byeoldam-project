package com.be.byeoldam.domain.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedUrlByTagRequest {
    private String tagName;
    private int cursorId;
    private int size;
}
