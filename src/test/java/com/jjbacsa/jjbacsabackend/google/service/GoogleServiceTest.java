package com.jjbacsa.jjbacsabackend.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.google.response.ShopQueryResponses;
import com.jjbacsa.jjbacsabackend.google.response.ShopResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
public class GoogleServiceTest {
    private final GoogleService googleService;

    double lat = 37.570429;
    double lng = 126.992095;
    String place_id = "ChIJx44qfNaYfDURtM0hCeeC7N4";

    @Test
    public void 쿼리_다중검색() throws JsonProcessingException {
        String test = "할리스";

        ShopQueryResponses shopQueryResponses = googleService.searchShopQuery(test, "cafe", lat, lng);
        Assertions.assertNotEquals(shopQueryResponses.getShopQueryResponseList().size(), 0);
    }

    @Test
    public void 상점_상세정보() throws JsonProcessingException {
        ShopResponse shopResponse = googleService.getShopDetails(place_id, lat, lng);
        Assertions.assertNotEquals(shopResponse.getDist(), null);
    }

}
