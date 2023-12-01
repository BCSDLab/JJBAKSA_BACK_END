package com.jjbacsa.jjbacsabackend.google.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 자동완성 결과 파싱 위한 메소드
 */

@Data
@NoArgsConstructor
public class Prediction {

    @Data
    public static class StructuredFormatting {
        @JsonProperty("main_text")
        String mainText;
    }

    @JsonProperty("structured_formatting")
    StructuredFormatting structuredFormatting;
}
