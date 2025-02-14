package com.be.byeoldam.domain.sharedcollection.dto;

import com.be.byeoldam.domain.bookmark.dto.TagDto;
import com.be.byeoldam.domain.bookmark.model.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SharedBookmarkResponse {
    private Long bookmarkId;

    private String url;
    private String img;
    private String title;
    private String description;

    private int readingTime;
    private boolean priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TagDto> tags;

    public static SharedBookmarkResponse of(Bookmark bookmark, List<TagDto> tagDtos) {
        return SharedBookmarkResponse.builder()
                .bookmarkId(bookmark.getId())
                .url(bookmark.getBookmarkUrl().getUrl())
                .img(bookmark.getBookmarkUrl().getImg())
                .title(bookmark.getBookmarkUrl().getTitle())
                .description(bookmark.getBookmarkUrl().getDescription())
                .priority(bookmark.isPriority())
                .readingTime(bookmark.getBookmarkUrl().getReadingTime())
                .createdAt(bookmark.getCreatedAt())
                .updatedAt(bookmark.getUpdatedAt())
                .tags(tagDtos)
                .build();
    }
}
