package com.jjbacsa.jjbacsabackend.review.dto;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Long id;
    private UserDto userDto;
    private ShopDto shopDto;
    private String content;
    private int isTemp;

}
