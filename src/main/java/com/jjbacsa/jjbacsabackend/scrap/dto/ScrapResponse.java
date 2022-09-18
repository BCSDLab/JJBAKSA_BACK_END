package com.jjbacsa.jjbacsabackend.scrap.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapResponse {

    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private ScrapDirectoryResponse directory;
    //Todo: ShopResponse 로 수정해야함
    private Long ShopId;
}
