package com.be.byeoldam.domain.bookmark.dto;

import com.be.byeoldam.domain.tag.model.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {

    @NotBlank(message = "태그 이름 입력은 필수입니다.")
    private String tagName;

    private String tagColor; // 태드 색상

    private String tagBolder; // 태그 테두리 색상

    public Tag toEntity() {
        return Tag.createTag(tagName, tagColor, tagBolder);
    }

    public static TagDto of(Tag tag) {
        return new TagDto(tag.getName(), tag.getColor(), tag.getBolderColor());
    }
}
