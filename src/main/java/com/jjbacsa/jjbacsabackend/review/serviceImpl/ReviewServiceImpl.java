package com.jjbacsa.jjbacsabackend.review.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.image.service.ImageService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final InternalUserService userService;
    private final InternalShopService shopService;
    private final InternalFollowService followService;
    // TODO: InternalService로 변경
    private final ImageService imageService;
    private final ReviewImageRepository reviewImageRepository;

    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest reviewRequest) throws Exception {
        ReviewEntity review = reviewRepository.save(createReviewEntity(reviewRequest));
        log.info(review.getCreatedAt().toString());
        return ReviewResponse.from(review);
    }

    @Override
    @Transactional
    public ReviewResponse modifyReview(ReviewRequest reviewRequest, Long reviewId) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        ReviewEntity review = reviewRepository.findByReviewId(reviewId);
        if(review == null) throw new RequestInputException(ErrorMessage.REVIEW_NOT_EXISTS_EXCEPTION);
        if(!review.getWriter().equals(userEntity)) throw new RequestInputException(ErrorMessage.INVALID_PERMISSION_REVIEW);
        if(reviewRequest.getContent() != null) review.setContent(reviewRequest.getContent());  // not null 컬럼
        modifyReviewInfo(review, reviewRequest);

        return ReviewResponse.from(review);
    }

    @Override
    @Transactional
    public ReviewDeleteResponse deleteReview(Long reviewId) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        ReviewEntity reviewEntity = reviewRepository.findByReviewId(reviewId);
        if(reviewEntity == null) throw new RequestInputException(ErrorMessage.REVIEW_NOT_EXISTS_EXCEPTION);
        if(!reviewEntity.getWriter().equals(userEntity)) throw new RequestInputException(ErrorMessage.INVALID_PERMISSION_REVIEW);

        for(ReviewImageEntity reviewImage: reviewEntity.getReviewImages()){ // 리뷰 이미지를 버킷에서 삭제
            imageService.deleteImage(reviewImage.getImage().getId());
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
    public ReviewResponse getReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findByReviewId(reviewId);
        if(review == null) throw new RequestInputException(ErrorMessage.REVIEW_NOT_EXISTS_EXCEPTION);
        return ReviewResponse.from(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchShopReviews(Long shopId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return reviewRepository.findAllByShopId(shopId, pageRequest).map(ReviewMapper.INSTANCE::fromReviewEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchWriterReviews(Long writerId,  Integer page, Integer size){
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return reviewRepository.findAllByWriterId(writerId, pageRequest).map(ReviewMapper.INSTANCE::fromReviewEntity);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(Integer page, Integer size) throws Exception {
        UserEntity user = userService.getLoginUser();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return reviewRepository.findAllByWriterId(user.getId(), pageRequest).map(ReviewMapper.INSTANCE::fromReviewEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getFollowersReviews(Integer page, Integer size) throws Exception {
        UserEntity user = userService.getLoginUser();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return reviewRepository.findAllFriendsReview(user.getId(), pageRequest).map(ReviewResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchFollowerReviews(String followerAccount, Integer page, Integer size) throws Exception {
        UserEntity user = userService.getLoginUser();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        UserEntity follower = userService.getUserByAccount(followerAccount);
        if(followService.existsByUserAndFollower(user, follower)){
            return reviewRepository.findAllByFollowerId(follower.getId(), pageRequest).map(ReviewResponse::from);
        }
        else throw new RequestInputException(ErrorMessage.NOT_FOLLOWED_EXCEPTION);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchFollowersShopReviews(Long shopId, Integer page, Integer size) throws Exception {
        UserEntity user = userService.getLoginUser();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return reviewRepository.findAllFollowersReviewsByShopId(user.getId(), shopId, pageRequest).map(ReviewResponse::from);
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

    private void modifyReviewInfo(ReviewEntity review, ReviewRequest reviewRequest) throws IOException {
        Integer curRate = review.getRate();
        Integer modRate = reviewRequest.getRate();

        if (modRate != null) {
            shopService.addTotalRating(review.getShop().getId(), modRate - curRate);
            review.setRate(modRate);
        }
        if(reviewRequest.getReviewImages() != null) {
            imageService.modifyReviewImages(reviewRequest.getReviewImages(), review);
        }
        else{
            if(review.getReviewImages() != null){
                for(ReviewImageEntity reviewImage: review.getReviewImages()){
                    imageService.deleteImage(reviewImage.getImage().getId());
                    reviewImageRepository.deleteById(reviewImage.getId());
                }
                review.getReviewImages().clear();
            }
        }
    }
}
