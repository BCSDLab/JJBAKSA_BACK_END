package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ShopQueryResponses {

    private String pageToken;
    private List<ShopQueryResponse> shopQueryResponseList;

    public ShopQueryResponses(String pageToken,List<ShopQueryResponse> shopQueryResponseList){
        this.pageToken= pageToken;
        this.shopQueryResponseList=shopQueryResponseList;
    }
}
