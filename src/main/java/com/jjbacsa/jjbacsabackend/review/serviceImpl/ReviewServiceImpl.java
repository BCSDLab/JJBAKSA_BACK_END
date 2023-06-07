package com.jjbacsa.jjbacsabackend.review.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.service.InternalGoogleService;
import com.jjbacsa.jjbacsabackend.review.dto.request.*;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewCountResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDateResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.mapper.ReviewMapper;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.review.service.ReviewService;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.service.InternalReviewImageService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final InternalUserService userService;
    private final InternalGoogleService shopService;
    private final InternalFollowService followService;
    private final InternalReviewImageService reviewImageService;

    private final ReviewRepository reviewRepository;

    @Override
    public ReviewResponse create(ReviewRequest reviewRequest) throws Exception {
        ReviewEntity review = reviewRepository.save(createReviewEntity(reviewRequest));
        return ReviewMapper.INSTANCE.fromReviewEntity(review);
    }

    @Override
    public ReviewResponse modify(ReviewRequest reviewRequest, Long reviewId) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        ReviewEntity review = reviewRepository.findByReviewId(reviewId);
        if (review == null) throw new RequestInputException(ErrorMessage.REVIEW_NOT_EXISTS_EXCEPTION);
        if (!review.getWriter().equals(userEntity))
            throw new RequestInputException(ErrorMessage.INVALID_PERMISSION_REVIEW);
        modifyReviewInfo(review, reviewRequest);

        return ReviewMapper.INSTANCE.fromReviewEntity(review);
    }

    @Override
    public ReviewDeleteResponse delete(Long reviewId) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        ReviewEntity reviewEntity = reviewRepository.findByReviewId(reviewId);
        if (reviewEntity == null) throw new RequestInputException(ErrorMessage.REVIEW_NOT_EXISTS_EXCEPTION);
        if (!reviewEntity.getWriter().equals(userEntity))
            throw new RequestInputException(ErrorMessage.INVALID_PERMISSION_REVIEW);

        for (ReviewImageEntity reviewImage : reviewEntity.getReviewImages()) { // 리뷰 이미지를 버킷에서 삭제
            reviewImageService.delete(reviewImage);
        }
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
    public ReviewResponse get(Long reviewId) throws Exception {
        UserEntity user = userService.getLoginUser();
        ReviewEntity review = reviewRepository.findByReviewId(reviewId);
        if (review == null) throw new RequestInputException(ErrorMessage.REVIEW_NOT_EXISTS_EXCEPTION);
        if (review.getWriter().getId().equals(user.getId()) || followService.existsByUserAndFollower(user, review.getWriter())) {
            return ReviewMapper.INSTANCE.fromReviewEntity(review);
        } else throw new RequestInputException(ErrorMessage.INVALID_PERMISSION_REVIEW);
    }

    @Override
    @Transactional(readOnly = true) // 내 리뷰가 존재하는 상점 반환
    public Page<ShopResponse> getShopsByMyReviews(ShopCursorRequest shopCursorRequest) throws Exception {
        UserEntity user = userService.getLoginUser();
        List<ShopResponse> shops = findShops(user.getId(), shopCursorRequest.getCursor(), shopCursorRequest.getSize());
        return PageableExecutionUtils.getPage(shops, PageRequest.ofSize(shopCursorRequest.getSize()), shops::size);
    }

    @Override
    @Transactional(readOnly = true) // 팔로워의 리뷰가 존재하는 상점
    public Page<ShopResponse> getShopsByFollowerReviews(ShopCursorRequest shopCursorRequest, Long followerId) throws Exception {
        UserEntity follower = userService.getUserById(followerId);
        UserEntity user = userService.getLoginUser();
        if (followService.existsByUserAndFollower(user, follower)) {
            List<ShopResponse> shops = findShops(follower.getId(), shopCursorRequest.getCursor(), shopCursorRequest.getSize());
            return PageableExecutionUtils.getPage(shops, PageRequest.ofSize(shopCursorRequest.getSize()), shops::size);
        } else throw new RequestInputException(ErrorMessage.NOT_FOLLOWED_EXCEPTION);
    }

    @Override
    @Transactional(readOnly = true) // 핀보기 리뷰
    public Page<ReviewResponse> getMyReviewsByShop(ReviewCursorRequest reviewCursorRequest, String placeId) throws Exception {
        UserEntity user = userService.getLoginUser();
        return reviewRepository.findAllByShopPlaceId(user.getId(), placeId, reviewCursorRequest)
                .map(ReviewMapper.INSTANCE::fromReviewEntityWithIgnoreImage);
    }

    @Override
    @Transactional(readOnly = true) // 핀보기 리뷰
    public Page<ReviewResponse> getFollowersReviewsByShop(ReviewCursorRequest reviewCursorRequest, String placeId) throws Exception {
        UserEntity user = userService.getLoginUser();
        return reviewRepository.findAllFollowersReviewsByShopPlaceId(user.getId(), placeId, reviewCursorRequest)
                .map(ReviewMapper.INSTANCE::fromReviewEntityWithIgnoreImage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getFollowerReviewsByShop(ReviewCursorRequest reviewCursorRequest, Long followerId, String placeId) throws Exception {
        UserEntity follower = userService.getUserById(followerId);
        UserEntity user = userService.getLoginUser();
        if (followService.existsByUserAndFollower(user, follower)) {
            return reviewRepository.findAllFollowerReviewsByShopPlaceId(follower.getId(), placeId, reviewCursorRequest)
                    .map(ReviewMapper.INSTANCE::fromReviewEntityWithIgnoreImage);
        } else throw new RequestInputException(ErrorMessage.NOT_FOLLOWED_EXCEPTION);
    }

    @Override
    @Transactional(readOnly = true) // 마이페이지 -> 총 리뷰 수
    public ReviewCountResponse getMyReviewCount() throws Exception {
        UserEntity user = userService.getLoginUser();
        Long count = reviewRepository.getReviewCount(user.getId());
        return ReviewCountResponse.builder().count(count == null ? 0L : count).build();
    }

    @Override
    @Transactional(readOnly = true) // 팔로워 마이페이지 -> 총 리뷰 수
    public ReviewCountResponse getFollowerReviewCount(Long followerId) throws Exception {
        UserEntity follower = userService.getUserById(followerId);
        UserEntity user = userService.getLoginUser();
        if (followService.existsByUserAndFollower(user, follower)) {
            Long count = reviewRepository.getReviewCount(follower.getId());
            return ReviewCountResponse.builder().count(count == null ? 0L : count).build();
        } else throw new RequestInputException(ErrorMessage.NOT_FOLLOWED_EXCEPTION);
    }

    @Override
    @Transactional(readOnly = true) // 상점에 대한 팔로워의 총 리뷰 수
    public ReviewCountResponse getFollowersReviewCount(String placeId) throws Exception {
        UserEntity user = userService.getLoginUser();
        Long count = reviewRepository.getFollowersReviewCountByShop(user.getId(), placeId);
        return ReviewCountResponse.builder().count(count == null ? 0L : count).build();
    }

    @Override
    @Transactional(readOnly = true) // 상점에 대한 팔로워의 마지막 리뷰 날짜
    public ReviewDateResponse getFollowerReviewLastDateByShop(String placeId) throws Exception {
        UserEntity user = userService.getLoginUser();
        Date date = reviewRepository.getFollowersReviewLastDateByShop(user.getId(), placeId);
        return ReviewDateResponse.builder().lastDate(date).build();
    }

    @Override
    @Transactional(readOnly = true) // 상점에 대한 내 마지막 리뷰 날짜
    public ReviewDateResponse getReviewLastDateByShop(String placeId) throws Exception {
        UserEntity user = userService.getLoginUser();
        Date date = reviewRepository.getReviewLastDateByShop(user.getId(), placeId);
        return ReviewDateResponse.builder().lastDate(date).build();
    }

    private ReviewEntity createReviewEntity(ReviewRequest reviewRequest) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        log.info(reviewRequest.getPlaceId());
        GoogleShopEntity shopEntity = shopService.getGoogleShopByPlaceId(reviewRequest.getPlaceId());
        ReviewEntity reviewEntity = ReviewEntity.builder()
                .writer(userEntity)
                .shop(shopEntity)
                .content(reviewRequest.getContent())
                .rate(reviewRequest.getRate())
                .build();

        if (reviewRequest.getReviewImages() != null) {
            List<ReviewImageEntity> reviewImageEntities = reviewImageService.createReviewImages(reviewRequest.getReviewImages());
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

    private void modifyReviewInfo(ReviewEntity review, ReviewRequest reviewRequest) throws IOException {
        review.update(reviewRequest);
        Integer curRate = review.getRate();
        Integer modRate = reviewRequest.getRate();

        if (modRate != null) {
            shopService.addTotalRating(review.getShop().getId(), modRate - curRate);
            review.setRate(modRate);
        }
        if (reviewRequest.getReviewImages() != null) {
            reviewImageService.modifyReviewImages(reviewRequest.getReviewImages(), review);
        } else {
            if (review.getReviewImages() != null) {
                for (ReviewImageEntity reviewImage : review.getReviewImages()) {
                    reviewImageService.delete(reviewImage);
                }
                review.getReviewImages().clear();
            }
        }
    }

    private List<ShopResponse> findShops(Long userId, Long cursor, int size) throws Exception {
        List<String> shopPlaceIds = reviewRepository.findShopPlaceIdsByMyReviews(userId, cursor, PageRequest.ofSize(size));
        return shopPlaceIds.stream()
                .map(placeId -> {
                    try {
                        return shopService.getShopDetails(placeId);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }
}
