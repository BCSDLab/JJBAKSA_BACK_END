package com.jjbacsa.jjbacsabackend.review.mapper;

<<<<<<< HEAD
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
=======
import com.jjbacsa.jjbacsabackend.review.dto.ReviewDto;
import com.jjbacsa.jjbacsabackend.review.dto.ReviewWithImageDto;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewWithImageResponse;
>>>>>>> review
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

<<<<<<< HEAD
    @Mapping(target = "reviewImages", ignore = true)
    @Mapping(source = "writer", target = "userReviewResponse")
    @Mapping(source = "shop", target ="shopReviewResponse")
    ReviewResponse fromReviewEntity(ReviewEntity reviewEntity);

    @Mapping(source = "writer", target = "userReviewResponse")
    @Mapping(source = "shop", target ="shopReviewResponse")
    ReviewDeleteResponse fromReviewEntityToDelete(ReviewEntity reviewEntity);


=======
    @Mapping(source = "userDto", target = "writer")
    @Mapping(source = "shopDto", target ="shop")
    ReviewEntity toReviewEntity(ReviewDto reviewDto);

    @Mapping(source = "writer", target = "userDto")
    @Mapping(source = "shop", target ="shopDto")
    ReviewResponse fromReviewEntity(ReviewEntity reviewEntity);

    @Mapping(source = "reviewImages", target = "reviewImageDtos")
    @Mapping(source = "writer", target = "userDto")
    @Mapping(source = "shop", target ="shopDto")
    ReviewWithImageResponse fromReviewEntityWithImages(ReviewEntity reviewEntity);


    @Mapping(source = "reviewImageDtos", target = "reviewImages")
    @Mapping(source = "userDto", target = "writer")
    @Mapping(source = "shopDto", target ="shop")
    ReviewEntity toReviewEntity(ReviewWithImageDto reviewWithReviewImageDto);

    @Mapping(source = "reviewImages", target = "reviewImageDtos")
    @Mapping(source = "writer", target = "userDto")
    @Mapping(source = "shop", target ="shopDto")
    ReviewWithImageResponse fromReviewEntityToReviewWithImages(ReviewEntity reviewEntity);
>>>>>>> review
}
