package com.jjbacsa.jjbacsabackend.shop.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TrendingResponse {
    List<String> trendings;
}