package com.jjbacsa.jjbacsabackend.etc.dto.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.text.Format.Field;
import java.util.List;

@Setter
@Getter
public class SlackAttachment {
    private String fallback;

    private String color;

    private String pretext;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_link")
    private String authorLink;

    @JsonProperty("author_icon")
    private String authorIcon;

    private String title;

    @JsonProperty("title_link")
    private String titleLink;

    private String text;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("thumb_url")
    private String thumbUrl;

    private String footer;

    @JsonProperty("footer_icon")
    private String footerIcon;

    private Long ts;

    private List<Field> fields;
}
