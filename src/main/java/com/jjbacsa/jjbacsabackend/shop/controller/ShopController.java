package com.jjbacsa.jjbacsabackend.shop.controller;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ShopController {

    private final ShopService shopService;

    @PostMapping(value="/shop/search")
    public ResponseEntity<List<ShopDto>> search(@RequestParam("query")String query, @RequestParam("x")String x, @RequestParam("y")String y){
        return new ResponseEntity<>(shopService.search(query,x,y), HttpStatus.OK);
    }
}
