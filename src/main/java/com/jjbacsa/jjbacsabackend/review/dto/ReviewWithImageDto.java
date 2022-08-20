package com.jjbacsa.jjbacsabackend.review.dto;

import com.jjbacsa.jjbacsabackend.review_image.dto.ReviewImageDto;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewWithImageDto {
    private Long id;
    private UserDto userDto;
    private ShopDto shopDto;
    private String content;
    private int isTemp;
    private List<ReviewImageDto> reviewImageDtos;
}
