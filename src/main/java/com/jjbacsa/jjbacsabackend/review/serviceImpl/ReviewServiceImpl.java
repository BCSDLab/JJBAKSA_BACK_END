package com.jjbacsa.jjbacsabackend.review.serviceImpl;

import com.jjbacsa.jjbacsabackend.review.dto.ReviewDto;
import com.jjbacsa.jjbacsabackend.review.dto.ReviewWithImageDto;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewWithImageResponse;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.mapper.ReviewMapper;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Override
    public ReviewWithImageResponse createReview(ReviewWithImageDto reviewWithImageDto) {
        ReviewEntity review = reviewRepository.save(ReviewMapper.INSTANCE.toReviewEntity(reviewWithImageDto));
        return ReviewMapper.INSTANCE.fromReviewEntityWithImages(review);
    }

    @Override
    public ReviewWithImageResponse modifyReview(ReviewWithImageDto reviewWithImageDto) {
        try {
            ReviewEntity review = reviewRepository.getById(reviewWithImageDto.getId());
            if (reviewWithImageDto.getContent() != null) review.setContent(reviewWithImageDto.getContent());  // not null 컬럼
            return ReviewMapper.INSTANCE.fromReviewEntityWithImages(review);
        }catch (EntityNotFoundException e){
            log.warn("리뷰 업데이트 실패, 리뷰를 찾을 수 없습니다 - dto: {}", reviewWithImageDto);
        }
        return null;
    }

    @Override
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewWithImageResponse getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .map(ReviewMapper.INSTANCE::fromReviewEntityToReviewWithImages)
                .orElseThrow(() -> new EntityNotFoundException("리뷰가 없습니다:" + reviewId));
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
}
