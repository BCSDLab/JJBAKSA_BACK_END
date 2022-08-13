package com.jjbacsa.jjbacsabackend.review.service;

import com.jjbacsa.jjbacsabackend.review.dto.ReviewDto;
import com.jjbacsa.jjbacsabackend.review.dto.ReviewWithImageDto;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewWithImageResponse;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.mapper.ReviewMapper;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.review.serviceImpl.ReviewServiceImpl;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@Slf4j
@DisplayName("비즈니스 로직 테스트 - 리뷰")
@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @InjectMocks private ReviewServiceImpl reviewService;

    @Mock private ReviewRepository reviewRepository;

    /*
        하나의 상점에 대해 테스트, Given, When, Then방식
            - 기능 -
        리뷰 생성
        리뷰 조회 (상점 기준, 작성자 기준 전체 조회)
        리뷰 단건 조회
        리뷰 수정
        리뷰 삭제
        //TODO:
        리뷰 임시저장
        임시저장 리뷰 조회
        임시 저장 리뷰 목록 조회
     */
    @DisplayName("리뷰 내용을 작성하면, 리뷰를 저장한다.")
    @Test
    void givenReviewInfo_whenWritingReview_thenCreateReview(){
        // Given
        ReviewWithImageDto dto = createReviewWithImageDto();
        given(reviewRepository.save(any(ReviewEntity.class))).willReturn(createReviewEntity(dto));

        // When
        ReviewWithImageResponse response = reviewService.createReview(dto);

        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("content", dto.getContent());
        then(reviewRepository).should().save(any(ReviewEntity.class));

    }
    @DisplayName("리뷰ID로 리뷰를 조회하면, 리뷰를 반환한다.")
    @Test
    void givenReviewId_whenSearchingReview_thenReturnReview(){
        // Given
        Long reviewId = 1L;
        ReviewWithImageDto dto = createReviewWithImageDto();
        ReviewEntity review = createReviewEntity(dto);
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // When
        ReviewWithImageResponse response = reviewService.getReview(reviewId);

        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("id", review.getId())
                .hasFieldOrPropertyWithValue("content", review.getContent())
                .hasFieldOrPropertyWithValue("isTemp", review.getIsTemp());
        then(reviewRepository).should().findById(reviewId);
    }
    @DisplayName("특정 상점에대한 전체 리뷰를 조회하면, 리뷰 페이지를 반환한다.")
    @Test
    void givenShopId_whenSearchingReviews_thenReturnsReviewPage(){
        // Given
        Long shopId = 1L;
        Pageable pageable = Pageable.ofSize(3);
        given(reviewRepository.findAllByShopId(shopId, pageable)).willReturn(Page.empty());
        // When
        Page<ReviewResponse> reviews = reviewService.searchShopReviews(shopId, pageable);

        // Then
        assertThat(reviews).isEmpty();
        then(reviewRepository).should().findAllByShopId(shopId, pageable);
    }

    @DisplayName("작성자 ID로 리뷰를 조회하면, 사용자가 작성한 리뷰 페이지를 반환한다.")
    @Test
    void givenUserId_whenSearchingReviews_thenReturnsReviewPage(){
        // Given
        Long writerId = 1L;
        Pageable pageable = Pageable.ofSize(3);
        given(reviewRepository.findAllByWriterId(writerId, pageable)).willReturn(Page.empty());
        // When
        Page<ReviewResponse> reviews = reviewService.searchWriterReviews(writerId, pageable);

        // Then
        assertThat(reviews).isEmpty();
        then(reviewRepository).should().findAllByWriterId(writerId, pageable);
    }

    @DisplayName("리뷰 내용을 수정하면, 리뷰를 수정한다.")
    @Test
    void givenModifiedReviewInfo_whenUpdatingReview_thenUpdatesReview(){

        // Given
        ReviewEntity review = createReviewEntity(createReviewWithImageDto());
        ReviewWithImageDto dto = createReviewWithImageDto("new content");
        given(reviewRepository.getById(dto.getId())).willReturn(review);

        // When
        reviewService.modifyReview(dto);

        // Then
        then(reviewRepository).should().getById(dto.getId());

    }

    @DisplayName("리뷰 아이디를 넘기면, 리뷰를 삭제한다.")
    @Test
    void givenReviewId_whenDeletingReview_thenDeletesReview(){
        // Given
        Long reviewId = 1L;
        willDoNothing().given(reviewRepository).deleteById(reviewId);
        // When
        reviewService.deleteReview(reviewId);
        // Then
        then(reviewRepository).should().deleteById(reviewId);
    }

    private ReviewEntity createReviewEntity(ReviewWithImageDto reviewWithImageDto){
        return ReviewMapper.INSTANCE.toReviewEntity(reviewWithImageDto);
    }


    private UserDto createUserDto(){
        return new UserDto(1L, "account", "email", "nickname");
    }
    private ShopDto createShopDto(){
        return new ShopDto(1L, "placeId", "placeName", "categoryName");
    }
    private ReviewWithImageDto createReviewWithImageDto(){
        return new ReviewWithImageDto(1L, createUserDto(), createShopDto(), "content", 0, LocalDateTime.now(), null);
    }
    private ReviewWithImageDto createReviewWithImageDto(String content){
        return new ReviewWithImageDto(1L, createUserDto(), createShopDto(), content, 0, LocalDateTime.now(), null);
    }

}
