package com.jjbacsa.jjbacsabackend.review.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.Auth;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewModifyRequest;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewModifyRequestDto;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewRequest;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewRequestDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/review")
    public ResponseEntity<ReviewResponse> createReview(@RequestPart ReviewRequestDto reviewRequestDto, @RequestPart(required = false) List<MultipartFile> images) throws Exception {
        ReviewRequest reviewRequest = ReviewRequest.createReviewRequest(images, reviewRequestDto);
        return new ResponseEntity<>(reviewService.createReview(reviewRequest), HttpStatus.CREATED);
    }
    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/review/{review_id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable("review_id") Long reviewId){
        return new ResponseEntity<>(reviewService.getReview(reviewId), HttpStatus.OK);
    }

    @GetMapping(value = "/review/search/shop")
    public ResponseEntity<Page<ReviewResponse>> searchShopReview(@RequestParam("id") Long shopId, @PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        return new ResponseEntity<>(reviewService.searchShopReviews(shopId, pageable), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/review/search/writer")
    public ResponseEntity<Page<ReviewResponse>>searchWriterReview(@RequestParam("id") Long writerId, @PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        return new ResponseEntity<>(reviewService.searchWriterReviews(writerId, pageable), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @DeleteMapping(value = "/review")
    public ResponseEntity<ReviewDeleteResponse>deleteReview(@RequestParam("delete-id") Long reviewId) throws Exception {
        return new ResponseEntity<>(reviewService.deleteReview(reviewId), HttpStatus.OK);
    }
    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value="/review")
    public ResponseEntity<ReviewResponse> modifyReview(@RequestPart ReviewModifyRequestDto reviewModifyRequestDto, @RequestPart(required = false) List<MultipartFile> images) throws Exception {
        ReviewModifyRequest reviewModifyRequest = ReviewModifyRequest.createReviewModifyRequest(images, reviewModifyRequestDto);
        return new ResponseEntity<>(reviewService.modifyReview(reviewModifyRequest), HttpStatus.OK);
    }

}
