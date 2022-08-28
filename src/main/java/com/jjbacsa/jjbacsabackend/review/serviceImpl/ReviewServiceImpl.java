package com.jjbacsa.jjbacsabackend.review.serviceImpl;

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
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;



@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final UserService userService;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
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
        UserEntity userEntity = userRepository.findById(userService.getLoginUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자 입니다."));
        ReviewEntity review = reviewRepository.findByReviewId(reviewModifyRequest.getId());

        if(review == null) throw new EntityNotFoundException("존재하지 않는 리뷰입니다. - review_id:" + reviewModifyRequest.getId());
        if(review.getWriter().getId() != userEntity.getId()) throw new RuntimeException("리뷰 작성자가 아닙니다.");
        if (reviewModifyRequest.getContent() != null) review.setContent(reviewModifyRequest.getContent());  // not null 컬럼

        if(reviewModifyRequest.getReviewImages() != null) {
            review = (imageService.modifyReviewImages(reviewModifyRequest.getReviewImages(), review));
        }
        else{
            if(review.getReviewImages() != null){
                for(ReviewImageEntity image: review.getReviewImages()){
                    reviewImageRepository.deleteById(image.getId());
                }
                review.getReviewImages().clear();
            }
        }
        return ReviewResponse.from(review);
    }

    @Override
    @Transactional
    public ReviewDeleteResponse deleteReview(Long reviewId) throws Exception {
        UserEntity userEntity = userRepository.findById(userService.getLoginUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자 입니다."));
        ReviewEntity reviewEntity = reviewRepository.findByReviewId(reviewId);
        if(reviewEntity == null) throw new RuntimeException("존재하지 않는 리뷰입니다. review_id: "+reviewId);
        if(reviewEntity.getWriter().getId() != userEntity.getId()) throw new RuntimeException("리뷰 작성자가 아닙니다.");
        reviewRepository.deleteById(reviewId);
        userEntity.getUserCount().decreaseReviewCount();
        return ReviewDeleteResponse.from(reviewEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findByReviewId(reviewId);
        if(review == null) throw new RuntimeException("존재하지 않는 리뷰입니다. review_id: "+reviewId);
        return ReviewResponse.from(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchShopReviews(Long shopId, Pageable pageable) {
        return reviewRepository.findAllByShopId(shopId, pageable).map(ReviewMapper.INSTANCE::fromReviewEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchWriterReviews(Long writerId, Pageable pageable){
        return reviewRepository.findAllByWriterId(writerId, pageable).map(ReviewMapper.INSTANCE::fromReviewEntity);
    }

    private ReviewEntity createReviewEntity(ReviewRequest reviewRequest) throws Exception {
        UserEntity userEntity = userRepository.findById(userService.getLoginUser().getId())
                                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자 입니다."));
        ShopEntity shopEntity = shopRepository.findById(reviewRequest.getShopId())
                                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상점입니다."));
        ReviewEntity reviewEntity = ReviewEntity.builder()
                .writer(userEntity)
                .shop(shopEntity)
                .content(reviewRequest.getContent())
                .isTemp(reviewRequest.getIsTemp())
                .build();

        if(reviewRequest.getReviewImages() != null) {
            List<ReviewImageEntity> reviewImageEntities = imageService.createReviewImages(reviewRequest.getReviewImages());
            for(ReviewImageEntity reviewImageEntity : reviewImageEntities){
                reviewEntity.addReviewImageEntity(reviewImageEntity);
            }
        }

        // 리뷰 수 증가
        reviewEntity.getWriter().getUserCount().increaseReviewCount();

        return reviewEntity;
    }
}
