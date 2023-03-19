package com.jjbacsa.jjbacsabackend.post.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import com.jjbacsa.jjbacsabackend.etc.dto.CustomPageRequest;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
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
    @GetMapping(value = "/post/{postId}")
    public ResponseEntity<PostResponse> get(@ApiParam("조회할 Post id") @PathVariable Long postId){
        return new ResponseEntity<>(postService.getPost(postId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "FAQ, NOTICE 작성",
            notes = "Post를 작성합니다.\n\n" +
                    "ADMIN 권한이 필요합니다.\n\n" +
                    "{\n\n" +
                    "       \"title\" : \"제목\"\n\n" +
                    "       \"content\" : \"내용\"\n\n"+
                    "       \"boardType\" : \"NOTICE, POWER_NOTICE, FAQ\"\n\n"+
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 201,
                    message = "작성한 Post 정보",
                    response = PostResponse.class)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/post")
    public ResponseEntity<PostResponse> create(@Validated(ValidationGroups.AdminCreate.class) @RequestBody PostRequest postRequest){
        return new ResponseEntity<>(postService.createPost(postRequest), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "FAQ, NOTICE 수정",
            notes = "Post를 수정합니다.\n\n" +
                    "ADMIN 권한이 필요합니다.\n\n" +
                    "{\n\n" +
                    "       \"postId\" : \"삭제할 Post의 id\"\"\n\n" +
                    "       \"title\" : \"제목\"\n\n" +
                    "       \"content\" : \"내용\"\n\n"+
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "수정한 Post 정보",
                    response = PostResponse.class)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/admin/post/{postId}")
    public ResponseEntity<PostResponse> modify(@RequestBody PostRequest postRequest, @ApiParam("수정할 Post Id") @PathVariable Long postId){
        return new ResponseEntity<>(postService.modifyAdminPost(postRequest, postId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "FAQ, NOTICE 삭제",
            notes = "ADMIN 권한이 필요합니다.\n\n" +
                    "{\n\n" +
                    "       \"postId\" : \"삭제할 Post의 id\"\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 204,
                    message = "반환값 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/admin/post/{postId}")
    public ResponseEntity<Void> delete(@ApiParam("삭제할 Post Id") @PathVariable Long postId){
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "Post 조회",
            notes = "Post를 조회합니다.\n\n"+
                    "example : \n\n"+
                    "{\n\n"+
                    "       \"boardType\" : \"BoardType은 NOTICE(공지) / FAQ, INQUERY는 문의하기페이지에 같이 게시)\"\n\n" +
                    "       \"page\" : \"페이지 default: 0\"\n\n" +
                    "       \"size\" : \"페이지 크기 default: 10\"\n\n" +
                    "       \"sort\": \"정렬 기준은 작성일 기준이며, POWER_NOTICE부터, 문의하기는 FAQ부터 반환됩니다. 값을 입력하지 않으셔도 됩니다.\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "Post Page",
                    response = Page.class)
    })
    @GetMapping(value = "/post")
    public ResponseEntity<Page<PostResponse>> getPosts(@RequestParam @Pattern(regexp = "^(FAQ|NOTICE|INQUIRY|POWER_NOTICE)$", message = "올바른 게시글 타입이 아닙니다.") String boardType, @Validated CustomPageRequest pageable){
        return new ResponseEntity<>(postService.getPosts(boardType, pageable.of()), HttpStatus.OK);
    }

}
