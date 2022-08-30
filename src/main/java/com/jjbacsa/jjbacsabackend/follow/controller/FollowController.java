package com.jjbacsa.jjbacsabackend.follow.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.Auth;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequestResponse;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowResponse;
import com.jjbacsa.jjbacsabackend.follow.service.FollowService;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class FollowController {

    private final FollowService service;

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/follow/requests")
    public ResponseEntity<FollowRequestResponse> requestFollow(
            @RequestBody FollowRequest request) throws Exception {

        return new ResponseEntity<>(service.request(request), HttpStatus.CREATED);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/follow/requests/send")
    public ResponseEntity<Page<FollowRequestResponse>> getSendRequests(
            @PageableDefault(size = 20) Pageable pageable) throws Exception {

        return new ResponseEntity<>(service.getSendRequests(pageable), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/follow/requests/receive")
    public ResponseEntity<Page<FollowRequestResponse>> getReceiveRequests(
            @PageableDefault(size = 20) Pageable pageable) throws Exception {

        return new ResponseEntity<>(service.getReceiveRequests(pageable), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @DeleteMapping(value = "/follow/requests/{request_id}/cancel")
    public ResponseEntity<Void> cancelFollow(
            @PathVariable("request_id") Long requestId) throws Exception {

        service.cancel(requestId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @DeleteMapping(value = "/follow/requests/{request_id}/reject")
    public ResponseEntity<Void> rejectFollow(
            @PathVariable("request_id") Long requestId) throws Exception {

        service.reject(requestId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/follow/requests/{request_id}/accept")
    public ResponseEntity<FollowResponse> acceptFollow(
            @PathVariable("request_id") Long requestId) throws Exception {

        return new ResponseEntity<>(service.accept(requestId), HttpStatus.CREATED);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/follow/followers")
    public ResponseEntity<Page<UserResponse>> getFollowers(
            @RequestParam(required = false) String cursor,
            @PageableDefault(size = 20) Pageable pageable) throws Exception {

        return new ResponseEntity<>(service.getFollowers(cursor, pageable), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @DeleteMapping(value = "/follow/followers")
    public ResponseEntity<Void> deleteFollow(
            @RequestBody FollowRequest request) throws Exception {

        service.delete(request);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
