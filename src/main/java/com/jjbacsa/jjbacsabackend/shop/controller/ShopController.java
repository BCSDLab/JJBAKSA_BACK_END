package com.jjbacsa.jjbacsabackend.shop.controller;

import com.jjbacsa.jjbacsabackend.shop.dto.Shop;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ShopController {

    private final ShopService shopService;

    @PostMapping(value="/shop")
    public ResponseEntity<Shop> getShop(@RequestParam("place_id")String placeId) throws ParseException {
        Long id=shopService.getShop(placeId);
        Shop shop=shopService.getShopDetails(placeId);
        return new ResponseEntity<>(shop,HttpStatus.OK);
    }

}
