package com.jjbacsa.jjbacsabackend.shop.controller;

import com.jjbacsa.jjbacsabackend.shop.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value="단일 상점 정보 조회",notes = "place_id에 대한 상점 상세정보를 조회한다.")
    @ApiImplicitParam(name="place_id",value="상점 아이디")
    public ResponseEntity<ShopResponse> getShop(@RequestParam("place_id")String placeId) {
        return new ResponseEntity<>(shopService.getShop(placeId), HttpStatus.OK);
    }

    @PostMapping(value="/shops")
    @ApiOperation(value="상점 검색",notes="입력받은 키워드와 현재 좌표를 기반으로 상점을 검색한다.")
    public ResponseEntity<Page<ShopSummaryResponse>> searchShop(@RequestBody ShopRequest shopRequest,
                                                                @PageableDefault(page =0,size=10) Pageable pageable){
        return new ResponseEntity<>(shopService.searchShop(shopRequest, pageable),HttpStatus.OK);
    }


}
