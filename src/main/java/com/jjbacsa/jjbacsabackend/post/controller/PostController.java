package com.jjbacsa.jjbacsabackend.post.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import com.jjbacsa.jjbacsabackend.etc.dto.CustomPageRequest;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostPageRequest;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostPageResponse;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import com.jjbacsa.jjbacsabackend.post.service.PostService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @ApiOperation(
            value = "Post 조회",
            notes = "Post를 조회합니다.\n\n" +
                    "{\n\n" +
                    "       \"postId\" : \"조회할 Post의 id\"\"\n\n" +
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
            value = "NOTICE 작성",
            notes = "Post를 작성합니다.\n\n" +
                    "ADMIN 권한이 필요합니다.\n\n" +
                    "{\n\n" +
                    "       \"title\" : \"제목\"\n\n" +
                    "       \"content\" : \"내용\"\n\n" +
                    "       \"boardType\" : \"NOTICE, POWER_NOTICE\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 201,
                    message = "작성한 Post 정보",
                    response = PostResponse.class)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/post")
    public ResponseEntity<PostResponse> create(@Validated(ValidationGroups.AdminCreate.class) @RequestBody PostRequest postRequest) {
        return new ResponseEntity<>(postService.createPost(postRequest), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "NOTICE 수정",
            notes = "Post를 수정합니다.\n\n" +
                    "ADMIN 권한이 필요합니다.\n\n" +
                    "{\n\n" +
                    "       \"postId\" : \"삭제할 Post의 id\"\"\n\n" +
                    "       \"title\" : \"제목\"\n\n" +
                    "       \"content\" : \"내용\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "수정한 Post 정보",
                    response = PostResponse.class)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/admin/post/{post-id}")
    public ResponseEntity<PostResponse> modify(@RequestBody PostRequest postRequest, @ApiParam("수정할 Post Id") @PathVariable("post-id") Long postId) {
        return new ResponseEntity<>(postService.modifyAdminPost(postRequest, postId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "NOTICE 삭제",
            notes = "ADMIN 권한이 필요합니다.\n\n" +
                    "{\n\n" +
                    "       \"postId\" : \"삭제할 Post의 id\"\"\n\n" +
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
            notes = "Post를 조회합니다.\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"cursor\" : \"마지막 조회한 post createdAt\"\n\n" +
                    "       \"size\" : \"페이지 크기 default: 3\"\n\n" +
                    "       \"boardType\" : \"NOTICE, POWER_NOTICE\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "Post Page",
                    response = Page.class)
    })
    @GetMapping(value = "/post")
    public ResponseEntity<Page<PostPageResponse>> getPosts(@Validated PostPageRequest postPageRequest) {
        return new ResponseEntity<>(postService.getPosts(postPageRequest), HttpStatus.OK);
    }

}
