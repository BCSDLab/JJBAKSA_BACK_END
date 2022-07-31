package com.jjbacsa.jjbacsabackend.rating.repository;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.rating.entity.RatingEntity;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RatingRepositoryTest {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private RatingRepository ratingRepository;


    private static UserEntity user;
    private static ShopEntity shop;
    private static RatingEntity rating;

    @BeforeAll
    static void init() {

        user = UserEntity.builder()
                .account("testuser")
                .password("password")
                .email("test@google.com")
                .nickname("testuser")
                .oAuthType(OAuthType.NONE)
                .userType(UserType.NORMAL)
                .build();

        shop = ShopEntity.builder()
                .placeId("abc")
                .placeName("testshop")
                .categoryName("test")
                .x("0")
                .y("0")
                .build();
    }

    @BeforeEach
    void eachInit() {

        UserEntity dbUser = userRepository.save(user);
        ShopEntity dbShop = shopRepository.save(shop);

        rating = RatingEntity.builder()
                .user(dbUser)
                .shop(dbShop)
                .ratingScore(5)
                .build();
    }

    @Test
    void findByUserAndShop() {

        ratingRepository.save(rating);
        Optional<RatingEntity> dbRating = ratingRepository.findByUserAndShop(rating.getUser(), rating.getShop());

        assertTrue(dbRating.isPresent());
        assertEquals(rating, dbRating.get());
    }

    @Test
    void existsByUserAndShop() {

        assertFalse(ratingRepository.existsByUserAndShop(rating.getUser(), rating.getShop()));

        ratingRepository.save(rating);

        assertTrue(ratingRepository.existsByUserAndShop(rating.getUser(), rating.getShop()));
    }
}