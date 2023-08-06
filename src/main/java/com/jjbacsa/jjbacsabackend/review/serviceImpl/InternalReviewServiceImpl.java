package com.jjbacsa.jjbacsabackend.review.serviceImpl;

import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.service.InternalGoogleService;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.review.service.InternalReviewService;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.service.InternalReviewImageService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InternalReviewServiceImpl implements InternalReviewService {

    private final ReviewRepository reviewRepository;
    private final InternalReviewImageService reviewImageService;
    private final InternalGoogleService shopService;

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
    @Transactional
    public void deleteReviewsWithUser(UserEntity user) {
        List<ReviewEntity> reviews = reviewRepository.findAllByWriter(user);

        for (ReviewEntity review : reviews) {

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

}
