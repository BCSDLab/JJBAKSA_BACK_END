package com.jjbacsa.jjbacsabackend.shop.dto.request;

import lombok.*;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRequest {
    private String keyword;
    private double x;
    private double y;
}
