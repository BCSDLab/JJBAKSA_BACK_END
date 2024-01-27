package com.jjbacsa.jjbacsabackend.google.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.BaseException;
import com.jjbacsa.jjbacsabackend.google.dto.api.SimpleShopDto;
import com.jjbacsa.jjbacsabackend.google.dto.request.AutoCompleteRequest;
import com.jjbacsa.jjbacsabackend.google.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.google.dto.response.*;
import com.jjbacsa.jjbacsabackend.google.service.GoogleShopService;
import com.jjbacsa.jjbacsabackend.google.dto.Category;
import com.jjbacsa.jjbacsabackend.search.service.SearchService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class GoogleShopController {

    private final GoogleShopService googleShopService;
    private final SearchService searchService;

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
                    "lat : 현재 요청자의 위도,\n     " +
                    "lng : 현재 요청자의 경도\n     " +
                    "\n}"
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "상점 키워드 검색 결과",
                    response = ShopQueryResponses.class
            )
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "상점 검색어", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category", value = "상점 카테고리", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping("/shops")
    public ResponseEntity<ShopQueryResponses> getGoogleShopsByType(@RequestBody @Valid ShopRequest shopRequest, @RequestParam(name = "keyword") String keyword, @RequestParam(name = "category") Category category) throws JsonProcessingException {
        searchService.saveTrending(keyword);

        return ResponseEntity.ok()
                .body(googleShopService.searchShopQuery(keyword, shopRequest, category));
    }

    @ExceptionHandler
    public ResponseEntity<BaseException> getGoogleShopsByTypeHandler(MethodArgumentTypeMismatchException e){
        BaseException exception = new BaseException(ErrorMessage.INVALID_REQUEST_EXCEPTION);

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(
            value = "키워드 검색 페이지 토큰 조회",
            notes = "키워드 검색으로 얻어진 페이지 토큰으로 상점들을 반환한다.\n\n" +
                    "Request Body(ShopRequest)\n\n" +
                    "{\n\n     " +
                    "lat : 현재 요청자의 위도,\n     " +
                    "lng : 현재 요청자의 경도\n     " +
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
                .body(googleShopService.getShopDetails(placeId));
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
    public ResponseEntity<ShopPinResponse> getPinGoogleShop(@PathVariable("place_id") String placeId) throws Exception {
        return ResponseEntity.ok()
                .body(googleShopService.getPinShop(placeId));
    }

    @ApiOperation(
            value = "필터를 통하여 저장된 상점 검색",
            notes = "메인 페이지 지도에 나타내기 위하여 간단하게 상점 정보를 반환한다.\n\n" +
                    "Request Body(ShopRequest)\n\n" +
                    "{\n\n     " +
                    "lat : 현재 요청자의 위도,\n     " +
                    "lng : 현재 요청자의 경도\n     " +
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

    @ApiOperation(
            value = "검색어 자동완성 ",
            notes = "음식점 검색 시에 자동완성으로 음식점 이름을 반환한다.\n\n" +
                    "Request Body(AutoCompleteRequest)\n\n" +
                    "{\n\n     " +
                    "lat : 현재 요청자의 위도,\n     " +
                    "lng : 현재 요청자의 경도\n     " +
                    "\n}"
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "자동완성 결과 (최대 5건)",
                    response = String.class,
                    responseContainer = "List"
            )
    })
    @ApiImplicitParam(
            name = "query", value = "자동완성 검색어", required = true, dataType = "string", paramType = "query"
    )
    @PostMapping("/shops/auto-complete")
    public ResponseEntity<List<String>> getAutoComplete(@RequestParam(name = "query") String query,
                                                        @RequestBody @Valid AutoCompleteRequest autoCompleteRequest) throws JsonProcessingException {
        return ResponseEntity.ok()
                .body(googleShopService.getAutoComplete(query, autoCompleteRequest));
    }

    @ApiOperation(
            value = "별점 조회",
            notes = "place Id를 바탕으로 별점을 반환한다.\n\n"
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "별점 데이터 반환",
                    response = ShopRateResponse.class
            )
    })
    @GetMapping("/shops/rates/{place_id}")
    public ResponseEntity<ShopRateResponse> getShopRate(@PathVariable("place_id") String placeId) {
        return ResponseEntity.ok()
                .body(googleShopService.getShopRate(placeId));
    }

    @ApiOperation(
            value = "스크랩 ID 조회",
            notes = "place Id를 바탕으로 스크랩 ID를 반환한다.\n\n"
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "스크랩 ID 반환",
                    response = ShopSimpleScrapResponse.class
            )
    })
    @GetMapping("/shops/scraps/{place_id}")
    public ResponseEntity<ShopSimpleScrapResponse> getShopSimpleScrap(@PathVariable("place_id") String placeId) throws Exception {
        return ResponseEntity.ok()
                .body(googleShopService.getSimpleShopScrap(placeId));
    }
}
