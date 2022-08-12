package com.jjbacsa.jjbacsabackend.shop.controller;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ShopController {

    private final ShopService shopService;

    /**
     * place_id로 화면에 나타낼 상점 정보 받아오기
     * 내부 로직은 서비스로 구현
     */
    @PostMapping(value="/shop")
    public ResponseEntity<ShopDto>getShop(@RequestParam("place_id")String place_id){
        return new ResponseEntity<>(shopService.getShop(place_id), HttpStatus.OK);
    }
    

}
