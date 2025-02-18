package com.be.byeoldam.domain.tag.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Optional;

public class JsoupUtil {
    private static final String DEFAULT_IMAGE_URL = "https://byeol-mypage.s3.ap-northeast-2.amazonaws.com/no_image.jpg";

    public static UrlPreview fetchMetadata(String url) {
        String title = "";
        String description = "";
        String imageUrl = DEFAULT_IMAGE_URL;

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(5000)
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .get();

            title = Optional.ofNullable(doc.head().selectFirst("title"))
                    .map(Element::text)
                    .orElse("");

            if (title.isEmpty()) {
                title = extractMetaContent(doc, "og:title", "twitter:title");
            }

            description = extractMetaContent(doc, "og:description", "twitter:description", "description");
            imageUrl = extractMetaContent(doc, "og:image", "twitter:image");

            if (description.isEmpty()) {
                description = Optional.ofNullable(doc.selectFirst("meta[name=description]"))
                        .map(meta -> meta.attr("content"))
                        .orElse("");
            }

            if (imageUrl.isEmpty()) {
                imageUrl = findFallbackImage(doc);
            }

            if (description.length() > 100) {
                description = description.substring(0, 100) + "...";
            }

        } catch (Exception e) {
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

    private static String extractMetaContent(Document doc, String... metaNames) {
        for (String metaName : metaNames) {
            String content = Optional.ofNullable(doc.selectFirst("meta[property=" + metaName + "]"))
                    .map(meta -> meta.attr("content"))
                    .orElse("");
            if (!content.isEmpty()) return content;
        }
        return "";
    }

    private static String findFallbackImage(Document doc) {
        String image = Optional.ofNullable(doc.selectFirst("link[rel=image_src]"))
                .map(element -> element.attr("href"))
                .orElse("");

        if (!image.isEmpty()) return image;

        Element firstImg = doc.selectFirst("img");
        if (firstImg != null) {
            return firstImg.absUrl("src");
        }

        return DEFAULT_IMAGE_URL;
    }
}
