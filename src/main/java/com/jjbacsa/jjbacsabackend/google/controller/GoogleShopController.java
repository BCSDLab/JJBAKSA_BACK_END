package com.jjbacsa.jjbacsabackend.google.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.google.dto.SimpleShopDto;
import com.jjbacsa.jjbacsabackend.google.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopQueryResponses;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopSimpleResponse;
import com.jjbacsa.jjbacsabackend.google.service.GoogleShopService;
import com.jjbacsa.jjbacsabackend.search.service.SearchService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class GoogleShopController {

    private final GoogleShopService googleShopService;
    private final SearchService searchService;
    private final String KEY = "RANKING";

    /**
     * 각 상점마다 검색시 상세조회 수행 (각각에 대해 상세조회 가격이 들어감<- 1000회 17달러)
     * ex) 한 쿼리에 대해 검색 결과가 100개 나왔으면 1.7달러
     * + place detail 같은 경우 상세 조회에 따라 가격 다르게 나옴
     * 거리순 정렬은 불가, 반경 radius 받아서 해당 반경에 있는 상점 반환 기능은 추후 추가 예정
     * google API 상점 조회 (리뷰 작성, 상점 조회시 사용)
     */

    @ApiOperation(
            value = "키워드에 따른 상점 검색",
            notes = "키워드로 상점 검색하여 상점들을 반환한다.\n\n" +
                    "Request Body(ShopRequest)\n\n" +
                    "{\n\n     " +
                    "lng : 현재 요청자의 경도,\n     " +
                    "lat : 현재 요청자의 위도\n     " +
                    "\n}"
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "상점 키워드 검색 결과",
                    response = ShopQueryResponses.class
            )
    })
    @ApiImplicitParam(
            name = "keyword", value = "상점 검색어", required = true, dataType = "string", paramType = "query"
    )
    @PostMapping("/shops")
    public ResponseEntity<ShopQueryResponses> getGoogleShopsByType(@RequestBody @Valid ShopRequest shopRequest, @RequestParam(name = "keyword") String keyword) throws JsonProcessingException {
        searchService.saveForAutoComplete(keyword);
        searchService.saveRedis(keyword, KEY);

        return ResponseEntity.ok()
                .body(googleShopService.searchShopQuery(keyword, shopRequest));
    }

    @ApiOperation(
            value = "키워드 검색 페이지 토큰 조회",
            notes = "키워드 검색으로 얻어진 페이지 토큰으로 상점들을 반환한다.\n\n" +
                    "Request Body(ShopRequest)\n\n" +
                    "{\n\n     " +
                    "lng : 현재 요청자의 경도,\n     " +
                    "lat : 현재 요청자의 위도\n     " +
                    "\n}"
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "상점 키워드 페이지 토큰 검색 결과",
                    response = ShopQueryResponses.class
            )
    })
    @ApiImplicitParam(
            name = "page_token", value = "키워드 검색어의 다음 페이지", required = true, dataType = "string", paramType = "path"
    )
    @PostMapping("/shops/page/{page_token}")
    public ResponseEntity<ShopQueryResponses> getGoogleShopsNextPage(@PathVariable("page_token") String pageToken,
                                                                     @RequestBody @Valid ShopRequest shopRequest) throws JsonProcessingException {
        return ResponseEntity.ok()
                .body(googleShopService.searchShopQueryNext(pageToken, shopRequest));
    }


    @ApiOperation(
            value = "단일 상점 조회",
            notes = "place_id를 기반으로 단일 상점 정보를 조회한다.\n\n"
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "단일 상점 조회 결과",
                    response = ShopResponse.class
            )
    })
    @ApiImplicitParam(name = "place_id", value = "단일 상점 검색 place id(Google)", required = true, dataType = "string", paramType = "path")
    @GetMapping("/shops/{place_id}")
    public ResponseEntity<ShopResponse> getGoogleShopDetails(@PathVariable("place_id") String placeId) throws Exception {

        return ResponseEntity.ok()
                .body(googleShopService.getShopDetails(placeId, true));
    }

    @ApiOperation(
            value = "핀보기 단일 상점 조회",
            notes = "place_id를 기반으로 핀보기 단일 상점 정보를 조회한다.\n\n"
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "핀보기 단일 상점 조회 결과",
                    response = ShopResponse.class
            )
    })
    @ApiImplicitParam(name = "place_id", value = "단일 상점 검색 place id(Google)", required = true, dataType = "string", paramType = "path")
    @GetMapping("/shops/pin/{place_id}")
    public ResponseEntity<ShopResponse> getPinGoogleShop(@PathVariable("place_id") String placeId) throws Exception {
        return ResponseEntity.ok()
                .body(googleShopService.getShopDetails(placeId, false));
    }

    @ApiOperation(
            value = "필터를 통하여 저장된 상점 검색",
            notes = "메인 페이지 지도에 나타내기 위하여 간단하게 상점 정보를 반환한다.\n\n" +
                    "Request Body(ShopRequest)\n\n" +
                    "{\n\n     " +
                    "lng : 현재 요청자의 경도,\n     " +
                    "lat : 현재 요청자의 위도\n     " +
                    "\n}"
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "지도 표시 위한 간단 상점 조회 결과",
                    response = SimpleShopDto.class,
                    responseContainer = "List"
            )
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "options_nearby", required = false, dataType = "Integer", value = "가까운 음식점 필터 여부"),
            @ApiImplicitParam(name = "options_friend", required = false, dataType = "Integer", value = "친구 음식점 필터 여부"),
            @ApiImplicitParam(name = "options_scrap", required = false, dataType = "Integer", value = "스크랩 음식점 필터 여부")
    })
    @PostMapping("/shops/maps")
    public ResponseEntity<List<ShopSimpleResponse>> getGoogleShops(@RequestParam(name = "options_nearby", required = false, defaultValue = "1") Integer nearBy,
                                                                   @RequestParam(name = "options_friend", required = false, defaultValue = "0") Integer friend,
                                                                   @RequestParam(name = "options_scrap", required = false, defaultValue = "0") Integer scrap,
                                                                   @RequestBody @Valid ShopRequest shopRequest) throws Exception {
        return ResponseEntity.ok()
                .body(googleShopService.getShops(nearBy, friend, scrap, shopRequest));
    }
}
