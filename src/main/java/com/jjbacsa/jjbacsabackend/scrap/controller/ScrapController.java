package com.jjbacsa.jjbacsabackend.scrap.controller;

import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopScrapResponse;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryResponse;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapResponse;
import com.jjbacsa.jjbacsabackend.scrap.service.ScrapService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Validated
//Todo: cursor를 String으로 받지 않고 parameter로 받아서 생성하기
public class ScrapController {

    private final ScrapService service;

    @ApiOperation(
            value = "스크랩 디렉토리 생성",
            notes = "이름이 같은 디렉토리를 생성할 수 없음\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"name\" : \"디렉토리 이름\"\n\n" +
                    "}",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping(value = "/scraps/directories")
    public ResponseEntity<ScrapDirectoryResponse> createDirectory(
            @RequestBody ScrapDirectoryRequest request) throws Exception {

        return new ResponseEntity<>(service.createDirectory(request), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "스크랩 디렉토리 목록 조회",
            notes = "커서 기반 페이징(마지막 객체로 조회)",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/scraps/directories")
    public ResponseEntity<Page<ScrapDirectoryResponse>> getDirectories(
            @RequestParam(required = false) String cursor,
            @ApiParam("가져올 데이터 수(1~100)") @Range(min = 1, max = 100, message = "1에서 100 사이여야 합니다.") @RequestParam(required = false, defaultValue = "10") Integer pageSize) throws Exception {

        return new ResponseEntity<>(service.getDirectories(cursor, pageSize), HttpStatus.OK);
    }

    @ApiOperation(
            value = "스크랩 디렉토리 수정",
            notes = "스크랩 디렉토리 이름 변경\n\n" +
                    "이름이 같은 디렉토리가 있으면 실패\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"name\" : \"디렉토리 이름\"\n\n" +
                    "}",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @PatchMapping(value = "/scraps/directories/{directory_id}")
    public ResponseEntity<ScrapDirectoryResponse> updateDirectory(
            @ApiParam("수정할 디렉토리 ID") @PathVariable("directory_id") Long directoryId,
            @RequestBody ScrapDirectoryRequest request) throws Exception {

        return new ResponseEntity<>(service.updateDirectory(directoryId, request), HttpStatus.OK);
    }

    @ApiOperation(
            value = "스크랩 디렉토리 삭제",
            notes = "디렉토리가 삭제될 때 하위 스크랩 모두 제거",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @DeleteMapping(value = "/scrap/directories/{directory_id}")
    public ResponseEntity<Void> deleteDirectory(
            @ApiParam("삭제할 디렉토리 ID") @PathVariable("directory_id") Long directoryId) throws Exception {

        service.deleteDirectory(directoryId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "스크랩 추가",
            notes = "directory id가 0일 경우 root에 저장\n\n" +
                    "같은 상점을 중복해서 추가할 수 없음\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"directoryId\" : 0,\n\n" +
                    "       \"shopId\" : 0\n\n" +
                    "}",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping(value = "/scraps")
    public ResponseEntity<ScrapResponse> create(
            @RequestBody ScrapRequest request) throws Exception {

        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "스크랩 목록 조회",
            notes = "directory id가 0일 경우 root 디렉토리 조회\n\n",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/scraps/directories/{directory_id}")
    public ResponseEntity<Page<ScrapResponse>> getScraps(
            @ApiParam("조회할 디렉토리 ID") @PathVariable(value = "directory_id", required = false) Long directoryId,
            @RequestParam(required = false) Long cursor,
            @ApiParam("가져올 데이터 수(1~100)") @Range(min = 1, max = 100, message = "1에서 100 사이여야 합니다.") @RequestParam(required = false, defaultValue = "10") Integer pageSize) throws Exception {

        return new ResponseEntity<>(service.getScraps(directoryId, cursor, pageSize), HttpStatus.OK);
    }

    @ApiOperation(
            value = "스크랩 이동",
            notes = "example : \n\n" +
                    "{\n\n" +
                    "       \"directoryId\" : 0\n\n" +
                    "}",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @PatchMapping(value = "/scraps/{scrap_id}")
    public ResponseEntity<ScrapResponse> move(
            @ApiParam("이동할 스크랩 ID") @PathVariable("scrap_id") Long scrapId,
            @RequestBody ScrapRequest request) throws Exception {

        return new ResponseEntity<>(service.move(scrapId, request), HttpStatus.OK);
    }

    @ApiOperation(
            value = "스크랩 삭제",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @DeleteMapping(value = "/scraps/{scrap_id}")
    public ResponseEntity<Void> delete(
            @ApiParam("삭제할 스크랩 ID") @PathVariable("scrap_id") Long scrapId) throws Exception {

        service.delete(scrapId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "유저 스크랩 상점 조회",
            notes = "userId가 null인 경우 현재 사용자의 상점 조회\n\n",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @GetMapping("/scraps")
    @PreAuthorize("hasRole('NORMAL')")
    public ResponseEntity <Page<ShopScrapResponse>> getUserScrapShops(@ApiParam("조회할 사용자 ID") @RequestParam(required=false, name = "user") Long userId,
                                                                      @RequestParam(required = false) Long cursor,
                                                                      @ApiParam("가져올 데이터 수(1~100)") @Range(min = 1, max = 100, message = "1에서 100 사이여야 합니다.") @RequestParam(required = false, defaultValue = "10") Integer pageSize) throws Exception {

        return ResponseEntity.ok(service.getScrapShops(userId, cursor, pageSize));
    }

}
