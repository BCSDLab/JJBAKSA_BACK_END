package com.jjbacsa.jjbacsabackend.shop.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRequest {
    @NotNull(message = "키워드를 비워둘 수 없습니다.")
    @NotBlank(message = "공백을 검색할 수 없습니다.")
    private String keyword;

    @NotNull(message = "경도를 비워둘 수 없습니다.")
    private double x;

    @NotNull(message = "위도를 비워둘 수 없습니다. ")
    private double y;
}
