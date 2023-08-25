package com.jjbacsa.jjbacsabackend.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jjbacsa.jjbacsabackend.google.dto.inner.Geometry;
import com.jjbacsa.jjbacsabackend.google.dto.inner.Photos;
import lombok.Data;

import java.util.List;

/**
 * 가까운 음식점, 친구 음식점, 북마크 음식점 DTO
 * <p>
 * - 좌표, 사진, 이름 반환
 */

@Data
public class SimpleShopDto {
    @JsonProperty("place_id")
    private String placeId;

    private String name;
    private Geometry geometry;
    private List<Photos> photos;
}
