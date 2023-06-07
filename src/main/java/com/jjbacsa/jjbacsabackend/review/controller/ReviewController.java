package com.jjbacsa.jjbacsabackend.review.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.review.dto.request.*;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewCountResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDateResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @ApiOperation(
            value = "리뷰 작성",
            notes = "리뷰를 작성합니다. MediaType은 MULTIPART_FORM_DATA_VALUE를 선택해주세요.\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"shopId\" : \"상점 id\"\n\n" +
                    "       \"content\" : \"내용\"\n\n" +
                    "       \"rate\" : \"별점\"\n\n" +
                    "       \"reviewImages\" : \"리뷰 이미지\"\n\n" +
                    "}",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping(value = "/review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> create(@Validated @ModelAttribute ReviewRequest reviewRequest) throws Exception {
        return new ResponseEntity<>(reviewService.create(reviewRequest), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "리뷰 조회",
            notes = "단건 리뷰를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/{review-id}")
    public ResponseEntity<ReviewResponse> get(@ApiParam("조회할 리뷰 id") @PathVariable("review-id") Long reviewId) throws Exception {
        return new ResponseEntity<>(reviewService.get(reviewId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "리뷰 삭제",
            notes = "리뷰를 삭제합니다.\n\n", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @DeleteMapping(value = "/review")
    public ResponseEntity<ReviewDeleteResponse> delete(@ApiParam("삭제할 리뷰 id") @RequestParam("delete-id") Long reviewId) throws Exception {
        return new ResponseEntity<>(reviewService.delete(reviewId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "리뷰 수정",
            notes = "리뷰를 수정합니다. MediaType은 MULTIPART_FORM_DATA_VALUE를 선택해주세요.\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"content\" : \"내용\"\n\n" +
                    "       \"rate\" : \"별점\"\n\n" +
                    "       \"reviewImages\" : \"리뷰 이미지\"\n\n" +
                    "       \"shopId\" : \"상점 id는 사용되지 않습니다.\"\n\n" +
                    "}",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @PutMapping(value = "/review/{review-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> modify(@Validated @ModelAttribute @RequestBody ReviewRequest reviewRequest, @ApiParam("수정할 리뷰 id") @PathVariable(name = "review-id") Long reviewId) throws Exception {
        return new ResponseEntity<>(reviewService.modify(reviewRequest, reviewId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "내 리뷰 목록 조회",
            notes = "내가 작성한 리뷰 목록을 조회합니다.\n\n" +
                    "커서 기반 페이징\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"placeId\" : \"조회할 상점 placeId\"\n\n" +
                    "       \"idCursor\" : \"조회한 마지막 리뷰 id, 첫 조회는 null\"\n\n" +
                    "       \"dateCursor\" : \"조회한 마지막 리뷰 createdAt, 정렬 기준이 createdAt일 때 입력\"\n\n" +
                    "       \"rateCursor\" : \"조회한 마지막 리뷰 rate, 정렬 기준이 rate일 때 입력\"\n\n" +
                    "       \"size\" : \"조회할 리뷰의 개수\"\n\n" +
                    "       \"direction\" : \"정렬방식 - 내림차순 desc, 오름차순 asc Default: desc\"\n\n" +
                    "       \"sort\" : \"정렬기준 - createdAt, rate Default: createdAt\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/shop/{shop-place-id}")
    public ResponseEntity<Page<ReviewResponse>> getMyReviewsByShop(@Validated ReviewCursorRequest reviewCursorRequest, @ApiParam("리뷰를 조회할 상점 place-id") @PathVariable(name = "shop-place-id") String placeId) throws Exception {
        return new ResponseEntity<>(reviewService.getMyReviewsByShop(reviewCursorRequest, placeId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "상점에 대해 모든 팔로워가 작성한 리뷰 목록 조회",
            notes = "상점에 대해 모든 팔로워가 작성한 리뷰 목록을 조회합니다.\n\n" +
                    "커서 기반 페이징\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"placeId\" : \"조회할 상점 placeId\"\n\n" +
                    "       \"idCursor\" : \"조회한 마지막 리뷰 id, 첫 조회는 null\"\n\n" +
                    "       \"dateCursor\" : \"조회한 마지막 리뷰 createdAt, 정렬 기준이 createdAt일 때 입력\"\n\n" +
                    "       \"rateCursor\" : \"조회한 마지막 리뷰 rate, 정렬 기준이 rate일 때 입력\"\n\n" +
                    "       \"size\" : \"조회할 리뷰의 개수\"\n\n" +
                    "       \"direction\" : \"정렬방식 - 내림차순 desc, 오름차순 asc Default: desc\"\n\n" +
                    "       \"sort\" : \"정렬기준 - createdAt, rate Default: createdAt\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/followers/shop/{shop-place-id}")
    public ResponseEntity<Page<ReviewResponse>> getFollowersReviewsByShop(@Validated ReviewCursorRequest reviewCursorRequest, @ApiParam("리뷰를 조회할 상점 place-id") @PathVariable(name = "shop-place-id") String placeId) throws Exception {
        return new ResponseEntity<>(reviewService.getFollowersReviewsByShop(reviewCursorRequest, placeId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "상점에 대해 팔로워가 작성한 리뷰 목록 조회",
            notes = "상점에 대해 팔로워가 작성한 리뷰 목록을 조회합니다.\n\n" +
                    "커서 기반 페이징\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"placeId\" : \"조회할 상점 placeId\"\n\n" +
                    "       \"idCursor\" : \"조회한 마지막 리뷰 id, 첫 조회는 null\"\n\n" +
                    "       \"dateCursor\" : \"조회한 마지막 리뷰 createdAt, 정렬 기준이 createdAt일 때 입력\"\n\n" +
                    "       \"rateCursor\" : \"조회한 마지막 리뷰 rate, 정렬 기준이 rate일 때 입력\"\n\n" +
                    "       \"size\" : \"조회할 리뷰의 개수\"\n\n" +
                    "       \"direction\" : \"정렬방식 - 내림차순 desc, 오름차순 asc Default: desc\"\n\n" +
                    "       \"sort\" : \"정렬기준 - createdAt, rate Default: createdAt\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/follower/{follower-id}/shop/{shop-place-id}")
    public ResponseEntity<Page<ReviewResponse>> getFollowerReviewsByShop(@Validated ReviewCursorRequest reviewCursorRequest, @ApiParam("팔로워 id") @PathVariable(name = "follower-id") Long followerId, @ApiParam("리뷰를 조회할 상점 place-id") @PathVariable(name = "shop-place-id") String placeId) throws Exception {
        return new ResponseEntity<>(reviewService.getFollowerReviewsByShop(reviewCursorRequest, followerId, placeId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "작성한 리뷰가 있는 상점 리스트 조회",
            notes = "작성한 리뷰가 있는 상점 리스트를 조회합니다.\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"cursor\" : \"조회한 마지막 상점 id\"\n\n" +
                    "       \"size\" : \"조회할 상점의 개수\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/shops")
    public ResponseEntity<Page<ShopResponse>> getShopsByMyReviews(@Validated ShopCursorRequest cursorRequest) throws Exception {
        return new ResponseEntity<>(reviewService.getShopsByMyReviews(cursorRequest), HttpStatus.OK);
    }

    @ApiOperation(
            value = "팔로워가 작성한 리뷰가 있는 상점 리스트 조회",
            notes = "팔로워가 작성한 리뷰가 있는 상점 리스트를 조회합니다.\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"cursor\" : \"조회한 마지막 상점 id\"\n\n" +
                    "       \"size\" : \"조회할 상점의 개수\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/follower/{follower-id}/shops")
    public ResponseEntity<Page<ShopResponse>> getShopsByFollowerReviews(@Validated ShopCursorRequest shopCursorRequest, @ApiParam("팔로워 id") @PathVariable(name = "follower-id") Long followerId) throws Exception {
        return new ResponseEntity<>(reviewService.getShopsByFollowerReviews(shopCursorRequest, followerId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "내 리뷰 개수 조회",
            notes = "내가 작성한 모든 리뷰의 개수를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/count")
    public ResponseEntity<ReviewCountResponse> getMyReviewCount() throws Exception {
        return new ResponseEntity<>(reviewService.getMyReviewCount(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "팔로워 리뷰 개수 조회",
            notes = "팔로워가 작성한 모든 리뷰의 개수를 조회합니다.\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"userAccount\" : \"조회할 팔로워 account\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/follower/{follower-id}/count")
    public ResponseEntity<ReviewCountResponse> getFollowerReviewCount(@ApiParam("팔로워 id") @PathVariable(name = "follower-id") Long followerId) throws Exception {
        return new ResponseEntity<>(reviewService.getFollowerReviewCount(followerId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "상점 내 모든 팔로워 리뷰 개수 조회",
            notes = "특정 상점에 대한 모든 팔로워가 작성한 리뷰의 개수를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/followers/count/shop/{shop-place-id}")
    public ResponseEntity<ReviewCountResponse> getFollowersReviewCountByShop(@PathVariable("shop-place-id") String placeId) throws Exception {
        return new ResponseEntity<>(reviewService.getFollowersReviewCount(placeId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "상점 내 팔로워 마지막 리뷰 날짜",
            notes = "특정 상점에 대해 팔로워가 작성한 마지막 리뷰의 날짜를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/followers/last-date/shop/{shop-place-id}")
    public ResponseEntity<ReviewDateResponse> getFollowerReviewLastDateByShop(@PathVariable("shop-place-id") String placeId) throws Exception {
        return new ResponseEntity<>(reviewService.getFollowerReviewLastDateByShop(placeId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "상점 내 나의 마지막 리뷰 날짜",
            notes = "특정 상점에 대해 내가 작성한 마지막 리뷰의 날짜를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/last-date/shop/{shop-place-id}")
    public ResponseEntity<ReviewDateResponse> getReviewLastDateByShop(@PathVariable("shop-place-id") String placeId) throws Exception {
        return new ResponseEntity<>(reviewService.getReviewLastDateByShop(placeId), HttpStatus.OK);
    }

}
