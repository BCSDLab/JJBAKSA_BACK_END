package com.jjbacsa.jjbacsabackend.scrap.repository;

import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ScrapRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoogleShopRepository googleShopRepository;
    @Autowired
    private ScrapRepository scrapRepository;
    @Autowired
    private ScrapDirectoryRepository scrapDirectoryRepository;

    private static UserEntity user;
    private static GoogleShopEntity shop;
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
                .userType(UserType.NORMAL)
                .build();

        shop = GoogleShopEntity.builder()
                .placeId("test")
                .build();

        directory = ScrapDirectoryEntity.builder()
                .name("dir1")
                .build();
    }

    @BeforeEach
    void eachInit() {

        UserEntity dbUser = userRepository.save(user);
        GoogleShopEntity dbShop = googleShopRepository.save(shop);
        ScrapDirectoryEntity dbDir = scrapDirectoryRepository.save(directory);

        scrap1 = ScrapEntity.builder()
                .user(dbUser)
                .shop(dbShop)
                .build();

        scrap2 = scrap1.toBuilder().directory(dbDir).build();

        scrapRepository.save(scrap1);
        scrapRepository.save(scrap2);
    }
}