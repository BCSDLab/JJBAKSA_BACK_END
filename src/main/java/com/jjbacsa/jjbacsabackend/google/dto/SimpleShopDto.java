package com.jjbacsa.jjbacsabackend.google.dto;

import com.jjbacsa.jjbacsabackend.google.dto.inner.Geometry;
import lombok.Data;

/**
 * 가까운 음식점, 친구 음식점, 북마크 음식점 DTO
 * <p>
 * - 좌표, 사진, 이름 반환
 */

//todo: s3에서 리뷰 사진 가져오기
@Data
public class SimpleShopDto {
    private String place_id;
    private String name;
    private Geometry geometry;
}
