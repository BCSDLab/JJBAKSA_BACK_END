package com.jjbacsa.jjbacsabackend.review.dto.response;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private UserDto userDto;
    private ShopDto shopDto;
    private String content;
    private int isTemp;
    private LocalDateTime createdAt;
}
