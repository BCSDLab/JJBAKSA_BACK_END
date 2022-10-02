package com.jjbacsa.jjbacsabackend.review.serviceImpl;

import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.image.service.ImageService;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewModifyRequest;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewRequest;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.mapper.ReviewMapper;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.review.service.ReviewService;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.repository.ReviewImageRepository;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.service.InternalShopService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ImageService imageService;
    private final InternalUserService userService;
    private final InternalShopService shopService;
    private final InternalFollowService followService;

    private final ReviewImageRepository reviewImageRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest reviewRequest) throws Exception {
        ReviewEntity review = reviewRepository.save(createReviewEntity(reviewRequest));
        return ReviewResponse.from(review);
    }

    @Override
    @Transactional
    public ReviewResponse modifyReview(ReviewModifyRequest reviewModifyRequest) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        ReviewEntity review = reviewRepository.findByReviewId(reviewModifyRequest.getId());
        if (review == null) throw new RuntimeException("존재하지 않는 리뷰입니다. - review_id:" + reviewModifyRequest.getId());
        if (!review.getWriter().equals(userEntity)) throw new RuntimeException("리뷰 작성자가 아닙니다.");
        if (reviewModifyRequest.getContent() != null)
            review.setContent(reviewModifyRequest.getContent());  // not null 컬럼
        modifyReviewInfo(review, reviewModifyRequest);

        return ReviewResponse.from(review);
    }

    @Override
    @Transactional
    public ReviewDeleteResponse deleteReview(Long reviewId) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        ReviewEntity reviewEntity = reviewRepository.findByReviewId(reviewId);
        if (reviewEntity == null) throw new RuntimeException("존재하지 않는 리뷰입니다. review_id: " + reviewId);
        if (!reviewEntity.getWriter().equals(userEntity)) throw new RuntimeException("리뷰 작성자가 아닙니다.");
        reviewRepository.deleteById(reviewId);

        // 리뷰 수, 별점 처리
        userService.decreaseReviewCount(userEntity.getId());
        Long shopId = reviewEntity.getShop().getId();
        shopService.addTotalRating(shopId, -reviewEntity.getRate());
        shopService.decreaseRatingCount(shopId);

        return ReviewDeleteResponse.from(reviewEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findByReviewId(reviewId);
        if (review == null) throw new RuntimeException("존재하지 않는 리뷰입니다. review_id: " + reviewId);
        return ReviewResponse.from(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchShopReviews(Long shopId, Pageable pageable) {
        return reviewRepository.findAllByShopId(shopId, pageable).map(ReviewMapper.INSTANCE::fromReviewEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchWriterReviews(Long writerId, Pageable pageable) {
        return reviewRepository.findAllByWriterId(writerId, pageable).map(ReviewMapper.INSTANCE::fromReviewEntity);
    }

    // TODO : searchWriterReviews와 searchShopReviews 필요한지??
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(Pageable pageable) throws Exception {
        UserEntity user = userService.getLoginUser();
        return reviewRepository.findAllByWriterId(user.getId(), pageable).map(ReviewMapper.INSTANCE::fromReviewEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getFollowersReviews(Pageable pageable) throws Exception {
        UserEntity user = userService.getLoginUser();
        return reviewRepository.findAllFriendsReview(user.getId(), pageable).map(ReviewResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchFollowerReviews(String followerAccount, Pageable pageable) throws Exception {
        UserEntity user = userService.getLoginUser();
        UserEntity follower = userService.getUserByAccount(followerAccount);
        if (followService.existsByUserAndFollower(user, follower)) {
            return reviewRepository.findAllByFollowerId(follower.getId(), pageable).map(ReviewResponse::from);
        } else throw new RuntimeException("친구 관계가 아닙니다. followerId : " + follower.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchFollowersShopReviews(Long shopId, Pageable pageable) throws Exception {
        UserEntity user = userService.getLoginUser();
        return reviewRepository.findAllFollowersReviewsByShopId(user.getId(), shopId, pageable).map(ReviewResponse::from);
    }

    private ReviewEntity createReviewEntity(ReviewRequest reviewRequest) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        ShopEntity shopEntity = shopService.getShopById(reviewRequest.getShopId());
        ReviewEntity reviewEntity = ReviewEntity.builder()
                .writer(userEntity)
                .shop(shopEntity)
                .content(reviewRequest.getContent())
                .rate(reviewRequest.getRate())
                .build();

        if (reviewRequest.getReviewImages() != null) {
            List<ReviewImageEntity> reviewImageEntities = imageService.createReviewImages(reviewRequest.getReviewImages());
            for (ReviewImageEntity reviewImageEntity : reviewImageEntities) {
                reviewEntity.addReviewImageEntity(reviewImageEntity);
            }
        }

        // 리뷰 수 증가
        userService.increaseReviewCount(userEntity.getId());
        // 상점 별점 증가
        shopService.addTotalRating(shopEntity.getId(), reviewRequest.getRate());
        shopService.increaseRatingCount(shopEntity.getId());

        return reviewEntity;
    }

    private void modifyReviewInfo(ReviewEntity review, ReviewModifyRequest reviewModifyRequest) throws IOException {
        Integer curRate = review.getRate();
        Integer modRate = reviewModifyRequest.getRate();

        if (modRate != null) {
            shopService.addTotalRating(review.getShop().getId(), modRate - curRate);
            review.setRate(modRate);
        }

        if (reviewModifyRequest.getReviewImages() != null) {
            imageService.modifyReviewImages(reviewModifyRequest.getReviewImages(), review);
        } else {
            if (review.getReviewImages() != null) {
                for (ReviewImageEntity image : review.getReviewImages()) {
                    reviewImageRepository.deleteById(image.getId());
                }
                review.getReviewImages().clear();
            }
        }
    }
}
