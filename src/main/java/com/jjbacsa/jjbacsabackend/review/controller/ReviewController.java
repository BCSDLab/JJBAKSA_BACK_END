package com.jjbacsa.jjbacsabackend.review.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.Auth;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewRequest;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> createReview(@ModelAttribute ReviewRequest reviewRequest) throws Exception {
        return new ResponseEntity<>(reviewService.createReview(reviewRequest), HttpStatus.CREATED);
    }
    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/review/{review-id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable("review-id") Long reviewId){
        return new ResponseEntity<>(reviewService.getReview(reviewId), HttpStatus.OK);
    }
    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/review")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(@RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @RequestParam(value = "size", required=false, defaultValue="3")Integer size) throws Exception {
        return new ResponseEntity<>(reviewService.getMyReviews(page, size), HttpStatus.OK);
    }

    @GetMapping(value = "/review/search/shop/{shop-id}")
    public ResponseEntity<Page<ReviewResponse>> searchShopReview(@PathVariable("shop-id") Long shopId, @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @RequestParam(value = "size", required=false, defaultValue="3")Integer size){
        return new ResponseEntity<>(reviewService.searchShopReviews(shopId, page, size), HttpStatus.OK);
    }

    @GetMapping(value = "/review/search/writer/{writer-id}")
    public ResponseEntity<Page<ReviewResponse>>searchWriterReview(@PathVariable("writer-id") Long writerId, @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @RequestParam(value = "size", required=false, defaultValue="3")Integer size){
        return new ResponseEntity<>(reviewService.searchWriterReviews(writerId, page, size), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @DeleteMapping(value = "/review")
    public ResponseEntity<ReviewDeleteResponse>deleteReview(@RequestParam("delete-id") Long reviewId) throws Exception {
        return new ResponseEntity<>(reviewService.deleteReview(reviewId), HttpStatus.OK);
    }
    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value="/review/{review-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> modifyReview(@ModelAttribute @RequestBody ReviewRequest reviewRequest, @PathVariable(name = "review-id") Long reviewId) throws Exception {
        return new ResponseEntity<>(reviewService.modifyReview(reviewRequest, reviewId), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value="/review/search/follower")
    public ResponseEntity<Page<ReviewResponse>> searchFollowerReview(@RequestParam("follower-account") String account, @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @RequestParam(value = "size", required=false, defaultValue="3")Integer size) throws Exception {
        return new ResponseEntity<>(reviewService.searchFollowerReviews(account, page, size), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value="/review/follower")
    public ResponseEntity<Page<ReviewResponse>> getFollowersReviews(@RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @RequestParam(value = "size", required=false, defaultValue="3")Integer size) throws Exception {
        return new ResponseEntity<>(reviewService.getFollowersReviews(page, size), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value="/review/search/shop/{shop-id}/follower")
    public ResponseEntity<Page<ReviewResponse>> searchFollowersShopReviews(@PathVariable("shop-id") Long shopId, @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @RequestParam(value = "size", required=false, defaultValue="3")Integer size) throws Exception {
        return new ResponseEntity<>(reviewService.searchFollowersShopReviews(shopId, page, size), HttpStatus.OK);
    }
}
