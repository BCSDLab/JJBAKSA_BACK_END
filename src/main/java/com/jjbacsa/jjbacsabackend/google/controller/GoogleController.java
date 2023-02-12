package com.jjbacsa.jjbacsabackend.google.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.google.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.google.response.ShopQueryResponses;
import com.jjbacsa.jjbacsabackend.google.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.service.GoogleService;
import com.jjbacsa.jjbacsabackend.search.service.SearchService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class GoogleController {

    private final GoogleService googleService;
    private final SearchService searchService;

    //    각 상점마다 검색시 상세조회 수행 (각각에 대해 상세조회 가격이 들어감<- 1000회 17달러)
//    ex) 한 쿼리에 대해 검색 결과가 100개 나왔으면 1.7달러
//    거리순 정렬은 불가, 반경 radius 받아서 해당 반경에 있는 상점 반환 기능은 추후 추가 예정
//    google API 상점 조회 (리뷰 작성, 상점 조회시 사용)

    @ApiOperation(
            value = "키워드에 따른 상점 검색",
            notes = "키워드로 상점 검색하여 상점들을 반환한다." +
                    "Request Body(ShopRequest)\n\n" +
                    "{\n\n     " +
                    "x : 현재 요청자의 경도(x),\n     " +
                    "y : 현재 요청자의 위도(y)\n     " +
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
            @ApiImplicitParam(
                    name = "type", value = "상점 카테고리(cafe, restaurant)", required = true, dataType = "string", paramType = "path"
            ),
            @ApiImplicitParam(
                    name = "keyword", value = "상점 검색어", required = true, dataType = "string", paramType = "path"
            )

    })
    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping("/google/shops/{type}/{keyword}")
    public ResponseEntity<ShopQueryResponses> getGoogleShopsByType(@RequestBody @Valid ShopRequest shopRequest, @PathVariable("type") String type, @PathVariable("keyword") String keyword) throws JsonProcessingException {
        searchService.saveForAutoComplete(keyword);
        searchService.saveRedis(keyword);

        return ResponseEntity.ok()
                .body(googleService.searchShopQuery(keyword, type, shopRequest.getX(), shopRequest.getY()));
    }

    @ApiOperation(
            value = "키워드 검색 페이지 토큰 조회",
            notes = "키워드 검색으로 얻어진 페이지 토근으로 상점들을 반환한다.\n\n" +
                    "Request Body(ShopRequest)\n\n" +
                    "{\n\n     " +
                    "x : 현재 요청자의 경도(x),\n     " +
                    "y : 현재 요청자의 위도(y)\n     " +
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
    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping("/google/shops/{page_token}")
    public ResponseEntity<ShopQueryResponses> getGoogleShopsNextPage(@RequestBody @Valid ShopRequest shopRequest, @PathVariable("page_token") String pageToken) throws JsonProcessingException {
        return ResponseEntity.ok()
                .body(googleService.searchShopQueryNext(pageToken, shopRequest.getX(), shopRequest.getY()));
    }

    @ApiOperation(
            value = "단일 상점 조회",
            notes = "place_id를 기반으로 단일 상점 정보를 조회한다." +
                    "Request Body(ShopRequest)\n\n" +
                    "{\n\n     " +
                    "x : 현재 요청자의 경도(x),\n     " +
                    "y : 현재 요청자의 위도(y)\n     " +
                    "\n}"
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "단일 상점 조회 결과",
                    response = ShopResponse.class
            )
    })
    @ApiImplicitParam(
            name = "place_id", value = "단일 상점 검색 place id(Google)", required = true, dataType = "string", paramType = "path"
    )
    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping("/google/shop/{place_id}")
    //google API 상점 상세정보 조회
    public ResponseEntity<ShopResponse> getGoogleShopDetails(@RequestBody @Valid ShopRequest shopRequest, @PathVariable("place_id") String placeId) throws JsonProcessingException {
        return ResponseEntity.ok()
                .body(googleService.getShopDetails(placeId, shopRequest.getX(), shopRequest.getY()));
    }

    //todo: image
//    @GetMapping("/photo/{photo_token}")
//    public ResponseEntity getGoogleShopPhoto(@PathVariable("photo_token") String photoToken) {
//
//        return ResponseEntity.ok().build();
//
////        byte[] byteArray = googleService.getShopPhoto(photoToken);
////
////
////        return ResponseEntity.ok()
////                .contentType(MediaType.IMAGE_JPEG)
////                //.contentType(MediaType.APPLICATION_OCTET_STREAM)
////                //.contentLength(Base64.getEncoder().encodeToString(byteArray).length())
////                //.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().build().toString())
////                //.body(byteArray);
////                //.body(Base64.getEncoder().encodeToString(byteArray));
////                .body(byteArray);
//    }

}
