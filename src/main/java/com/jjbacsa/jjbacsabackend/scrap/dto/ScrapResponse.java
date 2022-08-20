package com.jjbacsa.jjbacsabackend.scrap.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapResponse {

    private ScrapDirectoryResponse directory;
    //Todo: ShopResponse 로 수정해야함
    private Long ShopId;
}
