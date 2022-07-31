package com.jjbacsa.jjbacsabackend.scrap.repository;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.rating.entity.RatingEntity;
import com.jjbacsa.jjbacsabackend.rating.repository.RatingRepository;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ScrapRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ScrapRepository scrapRepository;
    @Autowired
    private ScrapDirectoryRepository scrapDirectoryRepository;

    private static UserEntity user;
    private static ShopEntity shop;
    private static ScrapDirectoryEntity directory;
    private static ScrapEntity scrap1;
    private static ScrapEntity scrap2;

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

        directory = ScrapDirectoryEntity.builder()
                .name("dir1")
                .build();
    }

    @BeforeEach
    void eachInit() {

        UserEntity dbUser = userRepository.save(user);
        ShopEntity dbShop = shopRepository.save(shop);
        ScrapDirectoryEntity dbDir = scrapDirectoryRepository.save(directory);

        scrap1 = ScrapEntity.builder()
                .user(dbUser)
                .shop(dbShop)
                .build();

        scrap2 = scrap1.toBuilder().directory(dbDir).build();

        scrapRepository.save(scrap1);
        scrapRepository.save(scrap2);
    }

    @Test
    void findAllByUserAndDirectory() {

        List<ScrapEntity> scraps = scrapRepository.findAllByUserAndDirectory(scrap2.getUser(),scrap2.getDirectory());

        assertEquals(scraps.get(0).getShop(),scrap2.getShop());
    }

    @Test
    void findAllByUserAndDirectoryIsNull() {

        List<ScrapEntity> scraps = scrapRepository.findAllByUserAndDirectoryIsNull(scrap1.getUser());

        assertEquals(scraps.get(0).getShop(),scrap1.getShop());
    }
}