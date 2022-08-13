package com.jjbacsa.jjbacsabackend.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Builder
@Getter
@AllArgsConstructor
public class Shop {
    private String placeId;
    private String placeName;
    private String x;
    private String y;
    private String categoryName;
    private String address;

    private Optional<String> phoneNumber;
    private Optional<String> weekdayText;
}