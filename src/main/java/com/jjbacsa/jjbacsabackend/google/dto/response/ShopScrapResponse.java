package com.jjbacsa.jjbacsabackend.google.dto.response;

import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryResponse;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ShopScrapResponse {
    private String placeId;
    private String name;
    private String photo;
    private String category;
    private Integer totalRating;
    private Integer ratingCount;
    private String address;

    //scrap
    private Long scrapId;
    private Date createdAt;
    private Date updatedAt;
    private ScrapDirectoryResponse directory;

    public void setShopCount(Integer totalRating, Integer ratingCount) {
        this.totalRating = totalRating;
        this.ratingCount = ratingCount;
    }

    public void setScrapInfo(Long scrapId, Date createdAt, Date updatedAt) {
        this.scrapId = scrapId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setDirectory(ScrapDirectoryResponse scrapDirectoryResponse){
        this.directory=scrapDirectoryResponse;
    }
}
