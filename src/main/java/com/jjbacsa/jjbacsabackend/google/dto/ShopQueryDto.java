package com.jjbacsa.jjbacsabackend.google.dto;

import lombok.Data;

import java.util.List;

/**
 * Query 로 얻어온 다중 상점 파싱
 * */

@Data
public class ShopQueryDto {
    String next_page_token;
    List<ShopQueryApiDto> results;
}
