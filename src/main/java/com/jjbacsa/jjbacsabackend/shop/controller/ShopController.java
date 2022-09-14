package com.jjbacsa.jjbacsabackend.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.shop.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ShopController {

    private final ShopService shopService;

    @GetMapping(value="/shop")
    public ResponseEntity<ShopResponse> getShop(@RequestParam("place_id")String placeId) throws JsonProcessingException {
        return new ResponseEntity<>(shopService.getShop(placeId), HttpStatus.OK);
    }

    @PostMapping(value="/shop/search")
    public ResponseEntity<Page<ShopSummaryResponse>> searchShop(@RequestBody ShopRequest shopRequest,
                                                                @RequestParam @PageableDefault(page =0,size=10) Pageable pageable){
        return new ResponseEntity<>(shopService.searchShop(shopRequest, pageable),HttpStatus.OK);
    }


}
