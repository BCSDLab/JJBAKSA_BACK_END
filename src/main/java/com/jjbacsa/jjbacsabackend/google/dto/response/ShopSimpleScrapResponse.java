package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShopSimpleScrapResponse {
    private Long scrapId;

    public static ShopSimpleScrapResponse createScrappedResponse(Long scrapId) {
        return new ShopSimpleScrapResponse(scrapId);
    }
}
