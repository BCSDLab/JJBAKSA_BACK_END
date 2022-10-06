package com.jjbacsa.jjbacsabackend.review.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.Auth;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewRequest;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<ReviewResponse> createReview(@ModelAttribute ReviewRequest reviewRequest) throws Exception {
        return new ResponseEntity<>(reviewService.createReview(reviewRequest), HttpStatus.CREATED);
    }
    @ApiOperation(
            value = "리뷰 조회",
            notes = "단건 리뷰를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/{review-id}")
    public ResponseEntity<ReviewResponse> getReview(@ApiParam("조회할 리뷰 id") @PathVariable("review-id") Long reviewId){
        return new ResponseEntity<>(reviewService.getReview(reviewId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "내 리뷰 조회",
            notes = "내가 작성한 리뷰를 모두 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(@ApiParam("조회할 페이지") @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @ApiParam("페이징 사이즈") @RequestParam(value = "size", required=false, defaultValue="3")Integer size) throws Exception {
        return new ResponseEntity<>(reviewService.getMyReviews(page, size), HttpStatus.OK);
    }

    // TODO: 관리자 권한으로 변경
    @ApiOperation(
            value = "상점에 대한 모든 리뷰 조회",
            notes = "상점에 대한 모든 리뷰를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    public ResponseEntity<Page<ReviewResponse>> searchShopReview(@ApiParam("조회할 상점id") @PathVariable("shop-id") Long shopId, @ApiParam("조회할 페이지") @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @ApiParam("페이징 사이즈") @RequestParam(value = "size", required=false, defaultValue="3")Integer size){
        return new ResponseEntity<>(reviewService.searchShopReviews(shopId, page, size), HttpStatus.OK);
    }

    // TODO: 관리자 권한으로 변경
    @ApiOperation(
            value = "특정사용자가 작성한 모든 리뷰 조회",
            notes = "사용자에 대한 모든 리뷰를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/review/search/writer/{writer-id}")
    public ResponseEntity<Page<ReviewResponse>>searchWriterReview(@ApiParam("작성자 id") @PathVariable("writer-id") Long writerId, @ApiParam("조회할 페이지") @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @ApiParam("페이징 사이즈") @RequestParam(value = "size", required=false, defaultValue="3")Integer size){
        return new ResponseEntity<>(reviewService.searchWriterReviews(writerId, page, size), HttpStatus.OK);
    }

    @ApiOperation(
            value = "리뷰 삭제",
            notes = "리뷰를 삭제합니다.\n\n", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @DeleteMapping(value = "/review")
    public ResponseEntity<ReviewDeleteResponse>deleteReview(@ApiParam("삭제할 리뷰 id") @RequestParam("delete-id") Long reviewId) throws Exception {
        return new ResponseEntity<>(reviewService.deleteReview(reviewId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "리뷰 수정",
            notes = "리뷰를 수정합니다. MediaType은 MULTIPART_FORM_DATA_VALUE를 선택해주세요.\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"shopId\" : \"상점 id\"\n\n" +
                    "       \"content\" : \"내용\"\n\n" +
                    "       \"rate\" : \"별점\"\n\n" +
                    "       \"reviewImages\" : \"리뷰 이미지\"\n\n" +
                    "}",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @PatchMapping(value="/review/{review-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> modifyReview(@ModelAttribute @RequestBody ReviewRequest reviewRequest, @ApiParam("수정할 리뷰 id") @PathVariable(name = "review-id") Long reviewId) throws Exception {
        return new ResponseEntity<>(reviewService.modifyReview(reviewRequest, reviewId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "팔로워 리뷰 조회",
            notes = "팔로워가 작성한 리뷰를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value="/review/search/follower")
    public ResponseEntity<Page<ReviewResponse>> searchFollowerReview(@ApiParam("팔로워 account") @RequestParam("follower-account") String account, @ApiParam("조회할 페이지") @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @ApiParam("페이징 사이즈") @RequestParam(value = "size", required=false, defaultValue="3")Integer size) throws Exception {
        return new ResponseEntity<>(reviewService.searchFollowerReviews(account, page, size), HttpStatus.OK);
    }

    @ApiOperation(
            value = "팔로워들이 작성한 모든 리뷰 조회",
            notes = "팔로워들이 작성한 모든 리뷰를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value="/review/follower")
    public ResponseEntity<Page<ReviewResponse>> getFollowersReviews( @ApiParam("조회할 페이지") @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @ApiParam("페이징 사이즈") @RequestParam(value = "size", required=false, defaultValue="3")Integer size) throws Exception {
        return new ResponseEntity<>(reviewService.getFollowersReviews(page, size), HttpStatus.OK);
    }

    @ApiOperation(
            value = "특정 상점에 대해 팔로워들이 작성한 리뷰 조회",
            notes = "특정 상점에 대해 팔로워들이 작성한 리뷰를 조회합니다.\n\n", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value="/review/search/shop/{shop-id}/follower")
    public ResponseEntity<Page<ReviewResponse>> searchFollowersShopReviews(@ApiParam("조회할 상점id") @PathVariable("shop-id") Long shopId, @ApiParam("조회할 페이지") @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @ApiParam("페이징 사이즈") @RequestParam(value = "size", required=false, defaultValue="3")Integer size) throws Exception {
        return new ResponseEntity<>(reviewService.searchFollowersShopReviews(shopId, page, size), HttpStatus.OK);
    }
}
