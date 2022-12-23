package com.jjbacsa.jjbacsabackend.post.controller;

import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import com.jjbacsa.jjbacsabackend.post.service.PostService;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/*

Notice
    getNotices (공지 보기)
FAQ
    getFAQs (FAQ 보기)

inquiry
    sendInquiry (문의하기)
Admin
    createNotice (공지 작성)
    modifyNotice (공지 수정)
    deleteNotice (공지 삭제)
    createFAQ (FAQ 작성)
    deleteFAQ (FAQ 삭제)
    modifyFAQ (FAQ 수정)
추가 필요

 */
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/post/{postId}")
    public ResponseEntity<PostResponse> get(@PathVariable Long postId){
        return new ResponseEntity<>(postService.getPost(postId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping(value = "/post")
    public ResponseEntity<PostResponse> createInquiry(@RequestBody PostRequest postRequest){
        return new ResponseEntity<>(postService.createPost(postRequest), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/post")
    public ResponseEntity<PostResponse> create(@RequestBody PostRequest postRequest){
        return new ResponseEntity<>(postService.createPost(postRequest), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/admin/post/{postId}")
    public ResponseEntity<PostResponse> modify(@RequestBody PostRequest postRequest, @PathVariable Long postId){
        return new ResponseEntity<>(postService.modifyAdminPost(postRequest, postId), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('NORMAL')")
    @PatchMapping(value = "/post/{postId}")
    public ResponseEntity<PostResponse> modifyInquiry(@RequestBody PostRequest postRequest, @PathVariable Long postId){
        return new ResponseEntity<>(postService.modifyUserPost(postRequest, postId), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/admin/post/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId){
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/post/faq")
    public ResponseEntity<Page<PostResponse>> getFAQs(@ApiParam("조회할 페이지") @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @ApiParam("페이징 사이즈") @RequestParam(value = "size", required=false, defaultValue="3")Integer size){
        return new ResponseEntity<>(postService.getFAQs(page,size), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/post/notice")
    public ResponseEntity<Page<PostResponse>> getNotices(@ApiParam("조회할 페이지") @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @ApiParam("페이징 사이즈") @RequestParam(value = "size", required=false, defaultValue="3")Integer size){
        return new ResponseEntity<>(postService.getNotices(page, size), HttpStatus.OK);
    }
}
