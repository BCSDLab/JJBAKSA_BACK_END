package com.jjbacsa.jjbacsabackend.review.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.review.service.InternalReviewService;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.service.InternalReviewImageService;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
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

    //todo: 구글 상점으로 변경되면 추후 수정
    @Override
    public List<Long> getReviewIdsForUser(Long userId) {

        return reviewRepository.findAllByWriterId(userId)
                .stream()
                .map(ReviewEntity::getShop)
                .map(ShopEntity::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewEntity> findReviewsByWriter(UserEntity user) {

        return reviewRepository.findAllByWriterId(user.getId());
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
