package com.jjbacsa.jjbacsabackend.review.service;


import com.jjbacsa.jjbacsabackend.image.service.ImageService;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewModifyRequest;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewRequest;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.review.serviceImpl.ReviewServiceImpl;


import com.jjbacsa.jjbacsabackend.review_image.repository.ReviewImageRepository;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserReviewResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import com.jjbacsa.jjbacsabackend.user.serviceImpl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@Slf4j
@DisplayName("리뷰 통합 테스트")
@SpringBootTest
@Sql(scripts = {"classpath:db/test/test_insert.sql"})
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
public class ReviewServiceTest {
    private final ReviewServiceImpl reviewService;
    @MockBean
    private final UserServiceImpl userService;

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    private static UserEntity user;
    private static UserEntity user2;


    /*
        하나의 상점에 대해 테스트, Given, When, Then방식
            - 기능 -
        리뷰 생성
        리뷰 조회 (상점 기준, 작성자 기준 전체 조회)
        리뷰 단건 조회
        리뷰 수정
        리뷰 삭제
     */
    @DisplayName("리뷰 내용을 작성하면, 리뷰를 저장한다.")
    @Test
    void givenReviewInfo_whenWritingReview_thenCreateReview() throws Exception {
        // Given
        user = userRepository.getById(1L);
        ReviewRequest dto = createReviewRequest();

        // When
        testLogin(user);
        ReviewResponse response = reviewService.createReview(dto);

        // Then
        assertThat(response.getShopReviewResponse().getId()).isEqualTo(dto.getShopId());
        assertThat(response.getContent()).isEqualTo(dto.getContent());
    }
    @DisplayName("리뷰ID로 리뷰를 조회하면, 리뷰를 반환한다.")
    @Test
    void givenReviewId_whenSearchingReview_thenReturnReview() throws Exception {
        // Given
        Long reviewId = 1L;
        ReviewRequest dto = createReviewRequest();
        ReviewEntity review = createReviewEntity(dto);

        // When
        ReviewResponse response = reviewService.getReview(reviewId);

        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("id", review.getId())
                .hasFieldOrPropertyWithValue("content", review.getContent())
                .hasFieldOrPropertyWithValue("rate", review.getRate());
        // 없는 리뷰에 대한 요청
        assertThrows(RuntimeException.class, ()-> reviewService.getReview(0L));
    }
    @DisplayName("특정 상점에대한 전체 리뷰를 조회하면, 리뷰 페이지를 반환한다.")
    @Test
    void givenShopId_whenSearchingReviews_thenReturnsReviewPage(){
        // Given
        Long shopId = 1L;
        Pageable pageable = Pageable.ofSize(3);
        // When
        Page<ReviewResponse> reviews = reviewService.searchShopReviews(shopId, pageable);
        Page<ReviewResponse> emptyReviews = reviewService.searchShopReviews(0L, pageable);

        // Then
        assertThat(reviews).isNotEmpty();
        assertThat(reviews).allMatch(review -> review.getId()>0);
        assertThat(emptyReviews).isEmpty();     // 없는 상점에 대해선 empty
    }

    @DisplayName("작성자 ID로 리뷰를 조회하면, 사용자가 작성한 리뷰 페이지를 반환한다.")
    @Test
    void givenUserId_whenSearchingReviews_thenReturnsReviewPage(){
        // Given
        Long writerId = 1L;
        Pageable pageable = Pageable.ofSize(3);

        // When
        Page<ReviewResponse> reviews = reviewService.searchWriterReviews(writerId, pageable);
        Page<ReviewResponse> emptyReviews = reviewService.searchWriterReviews(0L, pageable);

        // Then
        assertThat(reviews).isNotEmpty();
        assertThat(reviews).allMatch(review -> review.getUserReviewResponse().getId().equals(writerId));
        assertThat(emptyReviews).isEmpty();     // 없는 사용자에 대해선 empty
    }

    @DisplayName("리뷰 내용을 수정하면, 리뷰를 수정한다.")
    @Test
    void givenModifiedReviewInfo_whenUpdatingReview_thenUpdatesReview() throws Exception {

        // Given
        user = userRepository.getById(1L);
        String content = "new content";
        ReviewEntity review = createReviewEntity(createReviewRequest());
        ReviewModifyRequest dto = createReviewModifyRequest(content);

        // When
        testLogin(user);
        ReviewResponse response = reviewService.modifyReview(dto);

        // Then
        assertThat(review.getId()).isEqualTo(response.getId());
        assertThat(response.getContent()).isEqualTo(content);

        // 작성자가 아닌 경우
        user2 = userRepository.getById(2L);
        testLogin(user2);
        assertThrows(RuntimeException.class, ()-> reviewService.modifyReview(dto));

        // 없는 리뷰에 대해
        dto.setId(0L);
        assertThrows(RuntimeException.class, ()-> reviewService.modifyReview(dto));


    }

    @DisplayName("리뷰 아이디를 넘기면, 리뷰를 삭제한다.")
    @Test
    void givenReviewId_whenDeletingReview_thenDeletesReview() throws Exception {
        // Given
        user = userRepository.getById(1L);
        Long reviewId = 1L;

        // When
        testLogin(user);
        ReviewDeleteResponse response = reviewService.deleteReview(reviewId);

        // Then
        assertThat(response.getId()).isEqualTo(reviewId);

        // 없는 리뷰에 대해
        assertThrows(RuntimeException.class, () -> reviewService.deleteReview(0L));
        // 리뷰 작성자가 아닌경우
        user2 = userRepository.getById(2L);
        testLogin(user2);
        assertThrows(RuntimeException.class, ()-> reviewService.deleteReview(2L));

    }

    private ReviewEntity createReviewEntity(ReviewRequest request){
        UserEntity userEntity = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자 입니다."));
        ShopEntity shopEntity = shopRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상점입니다."));
        ReviewEntity reviewEntity = ReviewEntity.builder()
                .id(1L)
                .writer(userEntity)
                .shop(shopEntity)
                .content(request.getContent())
                .rate(request.getRate())
                .build();

        reviewEntity.getWriter().getUserCount().increaseReviewCount();

        return reviewEntity;
    }

    private ReviewRequest createReviewRequest(){
        return new ReviewRequest(1L, "content1", 3, null);
    }
    private ReviewModifyRequest createReviewModifyRequest(String content){
        return new ReviewModifyRequest(1L, 1L, content, 3, null);
    }
    private void testLogin(UserEntity user) throws Exception {

        when(userService.getLoginUser()).thenReturn(UserMapper.INSTANCE.toUserResponse(user));
    }

}
