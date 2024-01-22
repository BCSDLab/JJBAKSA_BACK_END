package com.jjbacsa.jjbacsabackend.google.dto.response;

import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryResponse;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ShopScrapResponse {
    private String placeId;
    private Long scrapId;
    private String name;
    private String photo;
    private String category;
    private String address;
    private Date createdAt;
    private Date updatedAt;
    private ShopRateResponse rate;

    public void setScrapInfo(final ScrapEntity scrap) {
        this.scrapId = scrap.getId();
        this.createdAt = scrap.getCreatedAt();
        this.updatedAt = scrap.getUpdatedAt();
    }
}
