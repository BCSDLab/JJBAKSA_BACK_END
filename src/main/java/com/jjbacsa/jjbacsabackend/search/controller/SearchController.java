package com.jjbacsa.jjbacsabackend.search.controller;

import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;
import com.jjbacsa.jjbacsabackend.search.service.SearchService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SearchController {
    private final SearchService searchService;

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
        return new ResponseEntity<>(searchService.getTrending(), HttpStatus.OK);
    }
}
