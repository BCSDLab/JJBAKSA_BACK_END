package com.jjbacsa.jjbacsabackend.review.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.Auth;
import com.jjbacsa.jjbacsabackend.review.dto.ReviewWithImageDto;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewWithImageResponse;
import com.jjbacsa.jjbacsabackend.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @Auth
    @PostMapping(value = "/review/create")
    public ResponseEntity<ReviewWithImageResponse> createReview(@RequestBody ReviewWithImageDto reviewWithImageDto){
        return new ResponseEntity<>(reviewService.createReview(reviewWithImageDto), HttpStatus.CREATED);
    }
    @Auth
    @GetMapping(value = "/review")
    public ResponseEntity<ReviewWithImageResponse> getReview(@RequestParam("get-id") Long reviewId){
        return new ResponseEntity<>(reviewService.getReview(reviewId), HttpStatus.OK);
    }
    @Auth
    @GetMapping(value = "/review/search-shop")
    public ResponseEntity<Page<ReviewResponse>> searchShopReview(@RequestParam("shop-id") Long shopId, @PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        return new ResponseEntity<>(reviewService.searchShopReviews(shopId, pageable), HttpStatus.OK);
    }

    @Auth
    @GetMapping(value = "/review/search-writer")
    public ResponseEntity<Page<ReviewResponse>>searchWriterReview(@RequestParam("writer-id") Long writerId, @PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        return new ResponseEntity<>(reviewService.searchWriterReviews(writerId, pageable), HttpStatus.OK);
    }

    @Auth
    @DeleteMapping(value = "/review")
    public ResponseEntity<HttpStatus>deleteReview(@RequestParam("delete-id") Long reviewId){
        reviewService.deleteReview(reviewId);
        return new ResponseEntity(HttpStatus.OK);
    }
    @Auth
    @PatchMapping(value="/review")
    public ResponseEntity<ReviewWithImageResponse> modifyReview(@RequestBody ReviewWithImageDto reviewWithImageDto){
        return new ResponseEntity<>(reviewService.modifyReview(reviewWithImageDto), HttpStatus.OK);
    }

}
