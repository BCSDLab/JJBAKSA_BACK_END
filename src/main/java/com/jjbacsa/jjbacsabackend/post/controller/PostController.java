package com.jjbacsa.jjbacsabackend.post.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import com.jjbacsa.jjbacsabackend.post.service.PostService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @ApiOperation(
            value = "Post 조회",
            notes = "post-id : 조회할 Post의 id")
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
                    "ADMIN 권한이 필요합니다.\n\n\t" +
                    "필요한 필드\n\n\t" +
                    "{\n\n     " +
                    "title : 제목,\n\n     " +
                    "content : 내용,\n\n     " +
                    "boardType : Post 타입(FAQ, NOTICE) \n\n\t}")
    @ApiResponses({
            @ApiResponse(code = 201,
                    message = "작성한 Post 정보",
                    response = PostResponse.class)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/post")
    public ResponseEntity<PostResponse> create(@Validated(ValidationGroups.Create.class) @RequestBody PostRequest postRequest){
        return new ResponseEntity<>(postService.createPost(postRequest), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "FAQ, NOTICE 수정",
            notes = "Post를 수정합니다.\n\n" +
                    "ADMIN 권한이 필요합니다.\n\n\t" +
                    "수정할 수 있는 필드\n\n\t" +
                    "{\n\n     " +
                    "title : 제목,\n\n     " +
                    "content : 내용 \n\n\t}     ")
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
                    "post-id : 삭제할 Post id"
                    )
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
            notes = "boardType : 조회할 Post의 id\n\n" +
                    "page: 조회할 Post page\n\n" +
                    "size: page의 size")
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "Post Page",
                    response = Page.class)
    })
    @GetMapping(value = "/post")
    public ResponseEntity<Page<PostResponse>> getPosts(@RequestParam String boardType, @ApiParam("조회할 페이지") @RequestParam(value = "page", required = false, defaultValue = "0")Integer page, @ApiParam("페이징 사이즈") @RequestParam(value = "size", required = false, defaultValue = "3")Integer size){
        return new ResponseEntity<>(postService.getPosts(boardType, page, size), HttpStatus.OK);
    }

}
