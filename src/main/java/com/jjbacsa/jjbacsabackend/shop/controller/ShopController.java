package com.jjbacsa.jjbacsabackend.shop.controller;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ShopController {

    private final ShopService shopService;

    @GetMapping(value="/shop")
    public ResponseEntity<ShopResponse> getShop(@RequestParam("place_id")String placeId) throws ParseException {
        return new ResponseEntity<>(shopService.getShop(placeId), HttpStatus.OK);
    }

}
