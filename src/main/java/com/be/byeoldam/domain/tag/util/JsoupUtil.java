package com.be.byeoldam.domain.tag.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;

public class JsoupUtil {
    public static UrlPreview fetchMetadata(String url) {
        String title="";
        String description="";
        String imageUrl="";

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(5000)
                    .get();

            title = doc.select("meta[property=og:title]").attr("content");
            imageUrl = doc.select("meta[property=og:image]").attr("content");
            description = doc.select("meta[property=og:description]").attr("content");
            // title = document.title();

            if (title.isEmpty()) {
                title = doc.title();
            }

            if (description.isEmpty()) {
                description = Optional.ofNullable(doc.selectFirst("meta[name=description]"))
                        .map(meta -> meta.attr("content"))
                        .orElse("");
            }

            if (imageUrl.isEmpty()) {
                imageUrl = Optional.ofNullable(doc.selectFirst("meta[property=og:image]"))
                        .map(meta -> meta.attr("content"))
                        .orElse("https://byeol-mypage.s3.ap-northeast-2.amazonaws.com/no_image.jpg");
            }

            if (description.length() > 100) {
                description = description.substring(0, 100) + "...";
            }

        } catch (IOException e) {
            e.printStackTrace();
            title = "";
            description = "";
            imageUrl = "https://byeol-mypage.s3.ap-northeast-2.amazonaws.com/no_image.jpg";
        }
        return UrlPreview.builder()
                .title(title)
                .description(description)
                .imageUrl(imageUrl)
                .build();
    }
}
