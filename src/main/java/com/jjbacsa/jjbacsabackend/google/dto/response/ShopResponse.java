package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * shopDto(단일 상점 자세한 정보) 반환되는 클래스
 */
@Builder
@Getter
public class ShopResponse {
    private String placeId;
    private String name;
    private String formattedAddress;
    private Coordinate coordinate;
    private String formattedPhoneNumber;
    private String category;
    private TodayPeriod todayPeriod;
    private List<String> photos;
}
