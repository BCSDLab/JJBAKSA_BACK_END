package com.jjbacsa.jjbacsabackend.google.dto.api.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class Photo {
    @JsonProperty("photo_reference")
    String photoReference;
}
