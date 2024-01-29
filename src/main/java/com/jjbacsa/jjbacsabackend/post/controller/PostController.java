package com.jjbacsa.jjbacsabackend.post.controller;

import com.jjbacsa.jjbacsabackend.post.dto.request.PostCursorRequest;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import com.jjbacsa.jjbacsabackend.post.service.PostService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @ApiOperation(
            value = "Post 조회",
            notes = "Post를 조회합니다.\n\n" +
                    "{\n\n" +
                    "       \"post-id\" : \"조회할 Post의 id\"\"\n\n" +
                    "}")
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "읽어온 Post 정보",
                    response = PostResponse.class)
    })
    @GetMapping(value = "/post/{post-id}")
    public ResponseEntity<PostResponse> get(@ApiParam("조회할 Post id") @PathVariable("post-id") Long postId) {
        return new ResponseEntity<>(postService.getPost(postId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "공지사항 작성",
            notes = "Post를 작성합니다.\n\n" +
                    "ADMIN 권한이 필요합니다.\n\n" +
                    "{\n\n" +
                    "       \"title\" : \"제목\"\n\n" +
                    "       \"content\" : \"내용\"\n\n" +
                    "       \"postImages\" : \"공지 이미지\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 201,
                    message = "작성한 Post 정보",
                    response = PostResponse.class)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> create(@Validated @ModelAttribute PostRequest postRequest) throws IOException {
        return new ResponseEntity<>(postService.createPost(postRequest), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "공지사항 수정",
            notes = "Post를 수정합니다.\n\n" +
                    "ADMIN 권한이 필요합니다.\n\n" +
                    "{\n\n" +
                    "       \"post-id\" : \"삭제할 Post의 id\"\"\n\n" +
                    "       \"title\" : \"제목\"\n\n" +
                    "       \"content\" : \"내용\"\n\n" +
                    "       \"postImages\" : \"공지 이미지\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "수정한 Post 정보",
                    response = PostResponse.class)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/admin/post/{post-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> modify(@Validated @ModelAttribute @RequestBody PostRequest postRequest, @ApiParam("수정할 Post Id") @PathVariable("post-id") Long postId) throws IOException {
        return new ResponseEntity<>(postService.modifyAdminPost(postRequest, postId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "공지사항 삭제",
            notes = "ADMIN 권한이 필요합니다.\n\n" +
                    "{\n\n" +
                    "       \"post-id\" : \"삭제할 Post의 id\"\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 204,
                    message = "반환값 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/admin/post/{post-id}")
    public ResponseEntity<Void> delete(@ApiParam("삭제할 Post Id") @PathVariable("post-id") Long postId) {
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "Post 조회",
            notes = "Post 목록을 조회합니다.\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"idCursor\" : \"마지막 조회한 post id\"\n\n" +
                    "       \"dateCursor\" : \"마지막 조회한 post createdAt\"\n\n" +
                    "       \"size\" : \"조회할 개수 default: 3\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "Post Page",
                    response = Page.class)
    })
    @GetMapping(value = "/post")
    public ResponseEntity<Page<PostResponse>> getPosts(@Validated PostCursorRequest postCursorRequest) {
        return new ResponseEntity<>(postService.getPosts(postCursorRequest), HttpStatus.OK);
    }

}
