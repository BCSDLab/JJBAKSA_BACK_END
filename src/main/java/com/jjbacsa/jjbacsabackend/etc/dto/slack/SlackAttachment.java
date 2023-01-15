package com.jjbacsa.jjbacsabackend.etc.dto.slack;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.text.Format.Field;
import java.util.List;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SlackAttachment {
    private String fallback;

    private String color;

    private String pretext;

    private String authorName;

    private String authorLink;

    private String authorIcon;

    private String title;

    private String titleLink;

    private String text;

    private String imageUrl;

    private String thumbUrl;

    private String footer;

    private String footerIcon;

    private Long ts;

    private List<Field> fields;
}
