package com.jjbacsa.jjbacsabackend.google.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AutoCompleteRequest {
    @NotNull(message = "경도를 비워둘 수 없습니다.")
    private double lng;

    @NotNull(message = "위도를 비워둘 수 없습니다. ")
    private double lat;
}
