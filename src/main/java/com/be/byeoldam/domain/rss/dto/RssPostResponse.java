package com.be.byeoldam.domain.rss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RssPostResponse {

    private String title;
    private String url;
    @JsonProperty("isRead")
    private boolean isRead;

    public static RssPostResponse of(String title, String url, boolean isRead) {
        return RssPostResponse.builder()
                .title(title)
                .url(url)
                .isRead(isRead)
                .build();
    }
}
