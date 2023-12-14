package com.jjbacsa.jjbacsabackend.google.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRequest {
    @NotNull(message = "경도를 비워둘 수 없습니다.")
    @Schema(example = "127")
    private double lng;

    @NotNull(message = "위도를 비워둘 수 없습니다. ")
    @Schema(example = "36")
    private double lat;
}
