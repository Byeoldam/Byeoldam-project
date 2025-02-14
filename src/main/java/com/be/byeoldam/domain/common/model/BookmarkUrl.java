package com.be.byeoldam.domain.common.model;

import com.be.byeoldam.domain.tag.util.UrlPreview;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="bookmark_url")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    private String title;

    private String img;

    private String description;

    @Column(name = "reference_count", nullable = false)
    private Long referenceCount;

    @Column(name = "reading_time")
    private int readingTime;

    @Builder
    private BookmarkUrl(String url, String title, String img, String description, Long referenceCount, int readingTime) {
        this.url = url;
        this.title = title;
        this.img = img;
        this.description = description;
        this.referenceCount = referenceCount;
        this.readingTime = readingTime;
    }

    public static BookmarkUrl create(UrlPreview preview, String url, Long referenceCount, int readingTime) {
        return BookmarkUrl.builder()
                .url(url)
                .title(preview.getTitle())
                .img(preview.getImageUrl())
                .description(preview.getDescription())
                .referenceCount(referenceCount)
                .readingTime(readingTime)
                .build();
    }

    public void decrement() {
        this.referenceCount -= 1;
    }

    public void increment() {
        this.referenceCount += 1;
    }
}
