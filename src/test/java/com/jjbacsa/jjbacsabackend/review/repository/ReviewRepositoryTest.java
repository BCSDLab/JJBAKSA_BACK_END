package com.jjbacsa.jjbacsabackend.review.repository;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    private static UserEntity user1;
    private static UserEntity user2;
    private static ShopEntity shop1;
    private static ShopEntity shop2;
    private static ReviewEntity review1_1;
    private static ReviewEntity review1_2;
    private static ReviewEntity review1_3;
    private static ReviewEntity review1_4;
    private static ReviewEntity review2_1;
    private static ReviewEntity review2_2;
    private static ReviewEntity review2_3;
    private static ReviewEntity review2_4;


    @BeforeAll
    static void init() {

        user1 = UserEntity.builder()
                .account("testuser")
                .password("password")
                .email("test@google.com")
                .nickname("testuser")
                .userType(UserType.NORMAL)
                .build();

        user2 = user1.toBuilder()
                .account("testuser2")
                .nickname("testuser2")
                .build();

        shop1 = ShopEntity.builder()
                .placeId("abc")
                .placeName("testshop")
                .categoryName("test")
                .x("0")
                .y("0")
                .build();

        shop2 = shop1.toBuilder()
                .placeId("def")
                .placeName("testshop2")
                .build();
    }

    @BeforeEach
    void eachInit() {

        UserEntity dbUser1 = userRepository.save(user1);
        UserEntity dbUser2 = userRepository.save(user2);
        ShopEntity dbShop1 = shopRepository.save(shop1);
        ShopEntity dbShop2 = shopRepository.save(shop2);

        review1_1 = ReviewEntity.builder()
                .writer(dbUser1)
                .shop(dbShop1)
                .content("review1_1")
                .build();

        review1_2 = review1_1.toBuilder()
                .shop(dbShop1)
                .content("review1_2")
                .build();
        review1_3 = review1_1.toBuilder()
                .shop(dbShop1)
                .content("review1_3")
                .build();
        review1_4 = review1_1.toBuilder()
                .shop(dbShop1)
                .content("review1_4")
                .build();


        review2_1 = review1_1.toBuilder()
                .writer(dbUser2)
                .content("review2_1")
                .build();

        review2_2 = review2_1.toBuilder()
                .shop(dbShop2)
                .content("review2_2")
                .build();
        review2_3 = review2_1.toBuilder()
                .shop(dbShop2)
                .content("review2_3")
                .build();
        review2_4 = review2_1.toBuilder()
                .shop(dbShop2)
                .content("review2_4")
                .build();

        reviewRepository.save(review1_1);
        reviewRepository.save(review1_2);
        reviewRepository.save(review1_3);
        reviewRepository.save(review1_4);
        reviewRepository.save(review2_1);
        reviewRepository.save(review2_2);
        reviewRepository.save(review2_3);
        reviewRepository.save(review2_4);

    }

    @Test
    void findAllByWriter() {
        Pageable pageable =Pageable.ofSize(3);
        Page<ReviewEntity> reviews1 = reviewRepository.findAllByWriterId(review1_1.getWriter().getId(), pageable);
        Page<ReviewEntity> reviews2 = reviewRepository.findAllByWriterId(review2_1.getWriter().getId(), pageable);

        for (ReviewEntity review : reviews1)
            assertEquals(review.getWriter(), review1_2.getWriter());
        for (ReviewEntity review : reviews2)
            assertEquals(review.getWriter(), review2_2.getWriter());
    }

    @Test
    void findAllByShop() {
        Pageable pageable =Pageable.ofSize(3);
        Page<ReviewEntity> reviews1 = reviewRepository.findAllByShopId(review1_1.getShop().getId(), pageable);
        Page<ReviewEntity> reviews2 = reviewRepository.findAllByShopId(review2_1.getShop().getId(), pageable);

        for (ReviewEntity review : reviews1)
            assertEquals(review.getShop(), review1_1.getShop());
        for (ReviewEntity review : reviews2)
            assertEquals(review.getShop(), review2_1.getShop());
    }

    @Test
    void findAllByWriterAndShop() {

        assertEquals(reviewRepository.findAllByWriterAndShop(review1_1.getWriter(), review1_1.getShop()).get(0).getContent(), review1_1.getContent());
        assertEquals(reviewRepository.findAllByWriterAndShop(review1_2.getWriter(), review1_2.getShop()).get(0).getContent(), review1_2.getContent());
        assertEquals(reviewRepository.findAllByWriterAndShop(review2_1.getWriter(), review2_1.getShop()).get(0).getContent(), review2_1.getContent());
        assertEquals(reviewRepository.findAllByWriterAndShop(review2_2.getWriter(), review2_2.getShop()).get(0).getContent(), review2_2.getContent());
    }
}