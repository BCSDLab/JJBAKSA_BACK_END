package com.jjbacsa.jjbacsabackend.review.mapper;

import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(target = "reviewImages", ignore = true)
    @Mapping(source = "writer", target = "userReviewResponse")
    @Mapping(source = "shop", target ="shopReviewResponse")
    ReviewResponse fromReviewEntity(ReviewEntity reviewEntity);

    @Mapping(source = "writer", target = "userReviewResponse")
    @Mapping(source = "shop", target ="shopReviewResponse")
    ReviewDeleteResponse fromReviewEntityToDelete(ReviewEntity reviewEntity);


}
