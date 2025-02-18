package com.be.byeoldam.domain.tag.dto;

import com.be.byeoldam.domain.tag.util.UrlPreview;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class RecommendedUrlResponse {
    private String url;
    private String title;
    private String description;
    private String imageUrl;
    private int readingTime;

    public RecommendedUrlResponse(String url, int readingTime, String description, String img, String title) {
        this.url = url;
        this.readingTime = readingTime;
        this.description = description;
        this.imageUrl = img;
        this.title = title;
    }

    public RecommendedUrlResponse updateFromPreview(UrlPreview preview){
        this.title = preview.getTitle();
        this.description = preview.getDescription();
        this.imageUrl = preview.getImageUrl();
        return this;
    }

}
