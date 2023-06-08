package com.jjbacsa.jjbacsabackend.google.repository;

import com.jjbacsa.jjbacsabackend.config.TestBeanConfig;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestBeanConfig.class)
public class GoogleRepositoryTest {

    @Autowired
    private GoogleShopRepository googleShopRepository;
    String place_id = "ChIJx44qfNaYfDURtM0hCeeC7N4";

    @Test
    public void 상점저장() {
        GoogleShopEntity shopEntity = GoogleShopEntity.builder()
                .placeId(place_id)
                .build();

        googleShopRepository.save(shopEntity);

        Assertions.assertNotNull(googleShopRepository.findByPlaceId(place_id));
    }

    @Test
    public void place_id로_상점_가져오기(){
        GoogleShopEntity shopEntity=GoogleShopEntity.builder()
                .placeId("test")
                .build();

        shopEntity=googleShopRepository.save(shopEntity);

        GoogleShopEntity googleShopEntityByPlaceId=googleShopRepository.findByPlaceId(shopEntity.getPlaceId())
                .orElse(null);

        Assertions.assertNotNull(googleShopEntityByPlaceId);
    }

    @Test
    public void google_shop_id로_상점_가져오기(){
        GoogleShopEntity shopEntity=GoogleShopEntity.builder()
                .placeId("test2")
                .build();

        shopEntity=googleShopRepository.save(shopEntity);

        GoogleShopEntity googleShopEntityById = googleShopRepository.findById(shopEntity.getId())
                .orElse(null);

        Assertions.assertNotNull(googleShopEntityById);
        Assertions.assertEquals(googleShopEntityById.getPlaceId(),"test2");
    }


}
