package com.jjbacsa.jjbacsabackend.shop.controller;

import com.jjbacsa.jjbacsabackend.shop.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;
import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class ShopController {

    private final ShopService shopService;

    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/shop/{placeId}")
    @ApiOperation(
            value = "단일 상점 정보 조회",
            notes = "place_id에 대한 상점 상세정보를 조회한다.",
            authorizations = @Authorization(value = "Bearer + accessToken")
    )
    @ApiImplicitParam(name = "placeId", value = "상점 아이디", dataType = "String", dataTypeClass = String.class, paramType = "path")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "조회한 단일 상점 정보",
                    response = ShopResponse.class
            )
    })
    public ResponseEntity<ShopResponse> getShop(@PathVariable("placeId") String placeId) {
        return new ResponseEntity<>(shopService.getShop(placeId), HttpStatus.OK);
    }

    @PostMapping(value = "/shops")
    @ApiOperation(
            value = "상점 검색",
            notes = "입력받은 키워드와 현재 좌표를 기반으로 상점을 검색한다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "검색할 키워드", required = true, dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "x", value = "현재 위치의 x좌표(경도)", required = true, dataType = "Double", dataTypeClass = Double.class),
            @ApiImplicitParam(name = "y", value = "현재 위치의 y좌표(위도)", required = true, dataType = "Double", dataTypeClass = Double.class),
            @ApiImplicitParam(name = "page", value = "조회할 페이지", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "size", value = "페이지 사이즈", dataType = "Integer", dataTypeClass = Integer.class)
    })
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "검색된 맛집 리스트",
                    response = ShopSummaryResponse.class,
                    responseContainer = "Page"
            )
    })
    public ResponseEntity<Page<ShopSummaryResponse>> searchShop(@RequestBody @Valid ShopRequest shopRequest,
                                                                @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return new ResponseEntity<>(shopService.searchShop(shopRequest, page, size), HttpStatus.OK);
    }
}
