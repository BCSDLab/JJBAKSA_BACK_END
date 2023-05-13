package com.jjbacsa.jjbacsabackend.google.dto;

import com.jjbacsa.jjbacsabackend.google.dto.inner.Geometry;
import lombok.Data;

/**
 * 가까운 음식점, 친구 음식점, 북마크 음식점 DTO
 * <p>
 * - 좌표, 사진, 이름 반환
 */

//todo: 사진 전송 (사진 하나만 보내면 됨)
@Data
public class SimpleShopDto {
    private String place_id;
    private String name;
    private Geometry geometry;
}
