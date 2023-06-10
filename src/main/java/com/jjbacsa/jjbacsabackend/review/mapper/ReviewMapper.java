package com.jjbacsa.jjbacsabackend.review.mapper;

import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review_image.dto.response.ReviewImageResponse;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mappings({
            @Mapping(source = "image.originalName", target = "originalName"),
            @Mapping(source = "image.url", target = "imageUrl")
    })
    ReviewImageResponse fromReviewImageEntity(ReviewImageEntity reviewImageEntity);

    @Mapping(source = "writer", target = "userReviewResponse")
    @Mapping(source = "shop.placeId", target ="shopPlaceId")
    ReviewResponse fromReviewEntity(ReviewEntity reviewEntity);

    @Mapping(target = "reviewImages", ignore = true)
    @Mapping(source = "writer", target = "userReviewResponse")
    @Mapping(source = "shop.placeId", target ="shopPlaceId")
    ReviewResponse fromReviewEntityWithIgnoreImage(ReviewEntity reviewEntity);


    @Mapping(source = "writer", target = "userReviewResponse")
    @Mapping(source = "shop", target ="shopReviewResponse")
    ReviewDeleteResponse fromReviewEntityToDelete(ReviewEntity reviewEntity);


}
