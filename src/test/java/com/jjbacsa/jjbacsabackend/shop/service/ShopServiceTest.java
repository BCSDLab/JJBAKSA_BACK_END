package com.jjbacsa.jjbacsabackend.shop.service;

import com.jjbacsa.jjbacsabackend.etc.exception.ApiException;
import com.jjbacsa.jjbacsabackend.shop.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**검색 부분(RollBack=false)*/

@DisplayName("상점 통합 테스트")
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestMethodOrder(value= MethodOrderer.OrderAnnotation.class)
@Transactional
public class ShopServiceTest {
    @Autowired
    private ShopService shopService;

    @Autowired
    StringRedisTemplate redisTemplate;

    private final String KEY="ranking";

    @Order(1)
    @DisplayName("상점정보 가져오기(성공)")
    @Test
    public void getShop(){
        //given
        String place_id="ChIJf3Rcr3xJZTURqPi9uX26q8c";

        //when
        ShopResponse response=shopService.getShop(place_id);

        //then
        assertThat(response.getPlaceId()).isEqualTo(place_id);
    }

    @Order(2)
    @DisplayName("유효하지 않은 place_id에 대한 상정점보 가져오기")
    @Test
    public void getShop_ApiException(){
        //given
        String place_id="test";

        //when, then
        Assertions.assertThrows(ApiException.class,()-> {
            shopService.getShop(place_id);
        });
    }

    /**상점 저장-> 검색 (rollback 없이 수행)*/

    @Order(3)
    @DisplayName("상점 검색 위한 저장(Rollback=false)")
    @Rollback(value = false)
    @Test
    public void setUpForSearch(){
        //given
        String place_id="ChIJf3Rcr3xJZTURqPi9uX26q8c";
        String place_id2="ChIJR56SPphLZTURk--ikmvfnME";
        String place_id3="ChIJK9Fe2N1LZTURigkvgpiRkyU";
        String place_id4= "ChIJX3Pl_91LZTUR2CcwGQPSxTI";

        //when
        shopService.getShop(place_id);
        shopService.getShop(place_id2);
        shopService.getShop(place_id3);
        shopService.getShop(place_id4);
    }

    @Order(4)
    @DisplayName("검색어에 카테고리 없는 일반검색")
    @Test
    public void searchWithoutCategory(){
        //given
        String keyword="대전카페";

        ShopRequest shopRequest=ShopRequest.builder()
                .keyword(keyword)
                .x(127.3922)
                .y(36.362)
                .build();

        //when
        Page<ShopSummaryResponse> res=shopService.searchShop(shopRequest,0,10);

        //then
        assertThat(res.stream().collect(Collectors.toList()).size()).isNotEqualTo(0);
    }

    @Order(5)
    @DisplayName("검색어에 카테고리 있는 일반검색")
    @Test
    public void searchWithCategory(){
        //given
        String keyword="크러쉬온드";

        ShopRequest shopRequest=ShopRequest.builder()
                .keyword(keyword)
                .x(127.3922)
                .y(36.362)
                .build();

        //when
        Page<ShopSummaryResponse> res=shopService.searchShop(shopRequest,0,10);

        //then
        assertThat(res.stream().collect(Collectors.toList()).size()).isNotEqualTo(0);
    }

    @Order(6)
    @DisplayName("인기검색어")
    @Test
    public void getTranding(){
        //then
        assertThat(shopService.getTrending().getTrendings().size()).isEqualTo(2);
        redisTemplate.opsForZSet().removeRange(KEY,0,-1); //redis KEY=ranking 삭제
    }
}
