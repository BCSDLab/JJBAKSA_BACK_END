package com.jjbacsa.jjbacsabackend.follow.controller;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequestResponse;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowResponse;
import com.jjbacsa.jjbacsabackend.follow.service.FollowService;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@RequiredArgsConstructor
@RestController
@Validated
//Todo: cursor를 String으로 받지 않고 parameter로 받아서 생성하기
public class FollowController {

    private final FollowService service;

    @ApiOperation(
            value = "팔로우 요청",
            notes = "중복으로 요청할 수 없음\n\n" +
                    "상대가 먼저 요청했을 경우 요청할 수 없음",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping(value = "/follow/requests")
    public ResponseEntity<FollowRequestResponse> requestFollow(
            @RequestBody FollowRequest request) throws Exception {

        return new ResponseEntity<>(service.request(request), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "보낸 팔로우 요청 조회",
            notes = "오프셋 기반 페이징(페이지 번호로 조회)",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/follow/requests/send")
    public ResponseEntity<Page<FollowRequestResponse>> getSendRequests(
            @ApiParam("가져올 데이터 수(1~100)") @Min(0) @RequestParam(required = false, defaultValue = "0") Integer page,
            @ApiParam("가져올 데이터 수(1~100)") @Range(min = 1, max = 100) @RequestParam(required = false, defaultValue = "20") Integer pageSize) throws Exception {

        return new ResponseEntity<>(service.getSendRequests(page, pageSize), HttpStatus.OK);
    }

    @ApiOperation(
            value = "받은 팔로우 요청 조회",
            notes = "오프셋 기반 페이징(페이지 번호로 조회)",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/follow/requests/receive")
    public ResponseEntity<Page<FollowRequestResponse>> getReceiveRequests(
            @ApiParam("가져올 데이터 수(1~100)") @Min(0) @RequestParam(required = false, defaultValue = "0") Integer page,
            @ApiParam("가져올 데이터 수(1~100)") @Range(min = 1, max = 100) @RequestParam(required = false, defaultValue = "20") Integer pageSize) throws Exception {

        return new ResponseEntity<>(service.getReceiveRequests(page, pageSize), HttpStatus.OK);
    }

    @ApiOperation(
            value = "팔로우 요청 취소",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @DeleteMapping(value = "/follow/requests/{request_id}/cancel")
    public ResponseEntity<Void> cancelFollow(
            @ApiParam(value = "팔로우 요청 ID") @PathVariable("request_id") Long requestId) throws Exception {

        service.cancel(requestId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "팔로우 요청 거절",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @DeleteMapping(value = "/follow/requests/{request_id}/reject")
    public ResponseEntity<Void> rejectFollow(
            @ApiParam(value = "팔로우 요청 ID") @PathVariable("request_id") Long requestId) throws Exception {

        service.reject(requestId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "팔로우 요청 수락",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping(value = "/follow/requests/{request_id}/accept")
    public ResponseEntity<FollowResponse> acceptFollow(
            @ApiParam(value = "팔로우 요청 ID") @PathVariable("request_id") Long requestId) throws Exception {

        return new ResponseEntity<>(service.accept(requestId), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "팔로워 목록 조회",
            notes = "커서 기반 페이징(마지막 객체로 조회)",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/follow/followers")
    public ResponseEntity<Page<UserResponse>> getFollowers(
            @RequestParam(required = false) String cursor,
            @ApiParam("가져올 데이터 수(1~100)") @Range(min = 1, max = 100) @RequestParam(required = false, defaultValue = "20") Integer pageSize) throws Exception {

        return new ResponseEntity<>(service.getFollowers(cursor, pageSize), HttpStatus.OK);
    }

    @ApiOperation(
            value = "팔로워 삭제",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @DeleteMapping(value = "/follow/followers")
    public ResponseEntity<Void> deleteFollow(
            @RequestBody FollowRequest request) throws Exception {

        service.delete(request);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
