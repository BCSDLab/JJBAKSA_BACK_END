package com.jjbacsa.jjbacsabackend.review.serviceImpl;

import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.review.service.InternalReviewService;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.service.InternalReviewImageService;
import com.jjbacsa.jjbacsabackend.shop.service.InternalShopService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InternalReviewServiceImpl implements InternalReviewService {

    private final ReviewRepository reviewRepository;
    private final InternalUserService userService;
    private final InternalShopService shopService;
    private final InternalReviewImageService reviewImageService;

    @Override
    public List<Long> getReviewIdsForUser(UserEntity user) {

        return reviewRepository.findAllByWriter(user)
                .stream()
                .map(ReviewEntity::getShop)
                .map(GoogleShopEntity::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewEntity> findReviewsByWriter(UserEntity user) {

        return reviewRepository.findAllByWriter(user);
    }


    @Override
    public void deleteReview(ReviewEntity review) throws Exception {

        for (ReviewImageEntity reviewImage : review.getReviewImages()) { // 리뷰 이미지를 버킷에서 삭제
            reviewImageService.delete(reviewImage);
        }
        review.setIsDeleted(1);

        // 리뷰 수, 별점 처리
        Long shopId = review.getShop().getId();
        shopService.addTotalRating(shopId, -review.getRate());
        shopService.decreaseRatingCount(shopId);
    }
}
