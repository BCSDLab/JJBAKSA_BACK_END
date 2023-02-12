package com.jjbacsa.jjbacsabackend.google.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRequest {
    @NotNull(message = "경도를 비워둘 수 없습니다.")
    private double x;

    @NotNull(message = "위도를 비워둘 수 없습니다. ")
    private double y;
}
