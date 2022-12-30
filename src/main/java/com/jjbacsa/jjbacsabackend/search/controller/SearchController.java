package com.jjbacsa.jjbacsabackend.search.controller;

import com.jjbacsa.jjbacsabackend.search.dto.AutoCompleteResponse;
import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;
import com.jjbacsa.jjbacsabackend.search.service.SearchService;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopResponse;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SearchController {

    private final SearchService searchService;
    private final String KEY="ranking";

    @GetMapping(value = "/auto-complete/{word}")
    @ApiOperation(
            value = "검색어 자동 완성",
            notes = "현재 검색어의 자동 완성 검색어를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "검색어 자동 완성 결과",
                    response = AutoCompleteResponse.class
            )
    })
    @ApiImplicitParam(name = "word", value = "검색어", dataType = "String", dataTypeClass = String.class, paramType = "path")
    public ResponseEntity<AutoCompleteResponse> getAutoCompletes(@PathVariable("word") String word) {
        return new ResponseEntity<>(searchService.getAutoCompletes(word), HttpStatus.OK);
    }


    @GetMapping(value = "/trending")
    @ApiOperation(
            value = "인기 검색어 조회",
            notes = "현재 시점의 인기 검색어를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "실시간 트렌드",
                    response = TrendingResponse.class
            )
    })
    public ResponseEntity<TrendingResponse> getTrending() {
        return new ResponseEntity<>(searchService.getTrending(KEY), HttpStatus.OK);
    }
}
