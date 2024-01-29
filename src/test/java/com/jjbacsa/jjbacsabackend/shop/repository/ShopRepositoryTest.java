package com.jjbacsa.jjbacsabackend.shop.repository;

import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 키워드 검색의 경우 Rollback:false
 * DB에 insert후 인덱싱 해야 함.
 * */

@DisplayName("상점 DB 테스트")
@SpringBootTest
@TestMethodOrder(value= MethodOrderer.OrderAnnotation.class)
class ShopRepositoryTest {

    @Autowired
    private ShopRepository shopRepository;

    private static ShopEntity shop;
    private static ShopEntity shop2;
    private static ShopEntity shop3;
    private static ShopEntity shop4;
    private static ShopEntity shop5;

    @BeforeAll
    static void init() {

        shop = ShopEntity.builder()
                .placeId("abc")
                .placeName("testshop")
                .categoryName("test")
                .x("0")
                .y("0")
                .build();

        shop2=ShopEntity.builder()
                .placeId("shop2")
                .placeName("떡볶이집")
                .address("서울특별시 동작구")
                .categoryName("restaurant")
                .x("0")
                .y("0")
                .build();

        shop3=ShopEntity.builder()
                .placeId("shop3")
                .placeName("홍대 떡볶이")
                .address("서울특별시 마포구 서교동")
                .categoryName("restaurant")
                .x("0")
                .y("0")
                .build();

        shop4=ShopEntity.builder()
                .placeId("shop4")
                .placeName("상수 떡볶이")
                .address("서울특별시 마포구 상수동")
                .categoryName("restaurant")
                .x("0")
                .y("0")
                .build();

        shop5=ShopEntity.builder()
                .placeId("shop5")
                .placeName("카페")
                .address("서울특별시 마포구 서교동")
                .categoryName("cafe")
                .x("0")
                .y("0")
                .build();
    }

    @Order(1)
    @DisplayName("정상 저장")
    @Test
    public void save(){
        ShopEntity storedShop2=shopRepository.save(shop2);
        assertEquals(storedShop2,shop2);
    }

    @Order(2)
    @DisplayName("저장 예외")
    @Test
    public void save_null(){
        assertThrows(DataIntegrityViolationException.class,() ->{
            shopRepository.save(shop);
        });
    }

    @Order(3)
    @Test
    void findByPlaceId() {

        shopRepository.save(shop2);
        Optional<ShopEntity> dbShop = shopRepository.findByPlaceId(shop2.getPlaceId());

        assertTrue(dbShop.isPresent());

        assertEquals(shop2,dbShop.get());
    }

    @Order(4)
    @Test
    void existsByPlaceId() {

        shopRepository.save(shop2);

        assertTrue(shopRepository.existsByPlaceId(shop2.getPlaceId()));
        assertFalse(shopRepository.existsByPlaceId("shop3"));
    }

    @Order(5)
    @DisplayName("상점 저장(Rollback=false), 상점 검색을 위한 사전 Test")
    @Rollback(value = false)
    @Test
    public void saveAll(){
        shopRepository.save(shop2);
        shopRepository.save(shop3);
        shopRepository.save(shop4);
        shopRepository.save(shop5);
    }

    @Order(6)
    @DisplayName("카테고리가 없는 상점 검색")
    @Test
    public void search(){
        //given
        String keyword="상수 수동 동떡 떡볶 볶이";

        //when
        List<ShopSummaryResponse> searchResult=shopRepository.search(keyword,null);

        //then
        assertEquals(3,searchResult.size());
    }

    @Order(7)
    @DisplayName("카테고리가 있는 상점 검색")
    @Test
    public void searchWithCategory(){
        //given
        String keyword="서울 울카 카페";

        //when
        List<ShopSummaryResponse>searchResult=shopRepository.search(keyword,"cafe");

        //then
        assertEquals(1,searchResult.size());

    }

    @Order(8)
    @DisplayName("카테고리로 상점 검색")
    @Test
    public void findAllByCategoryName(){
        //given
        String category="cafe";

        //when
        List<ShopSummaryResponse> searchResult=shopRepository.findAllByCategoryName(category);

        //then
        assertEquals(1,searchResult.size());
    }

    @Order(9)
    @DisplayName("한글자로 상점 검색")
    @Test
    public void findByPlaceNameContaining(){
        //given
        String keyword="떡";

        //when
        List<ShopSummaryResponse> searchResult=shopRepository.findByPlaceNameContaining(keyword);

        //then
        assertEquals(3,searchResult.size());

    }
}