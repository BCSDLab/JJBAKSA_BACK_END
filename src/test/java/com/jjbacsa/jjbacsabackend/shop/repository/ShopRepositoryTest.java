package com.jjbacsa.jjbacsabackend.shop.repository;

import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShopRepositoryTest {

    @Autowired
    private ShopRepository shopRepository;

    private static ShopEntity shop;

    @BeforeAll
    static void init() {

        shop = ShopEntity.builder()
                .placeId("abc")
                .placeName("testshop")
                .categoryName("test")
                .x("0")
                .y("0")
                .build();
    }

    @Test
    void findByPlaceId() {

        shopRepository.save(shop);
        Optional<ShopEntity> dbShop = shopRepository.findByPlaceId(shop.getPlaceId());

        assertTrue(dbShop.isPresent());
        assertEquals(shop, dbShop.get());
    }

    @Test
    void existsByPlaceId() {

        shopRepository.save(shop);

        assertTrue(shopRepository.existsByPlaceId(shop.getPlaceId()));
        assertFalse(shopRepository.existsByPlaceId("test2"));
    }
}