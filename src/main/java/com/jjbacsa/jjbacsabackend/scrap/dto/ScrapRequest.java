package com.jjbacsa.jjbacsabackend.scrap.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapRequest {

    private Long directoryId;
    private Long shopId;
}
