package com.jjbacsa.jjbacsabackend.follow.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.Auth;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.service.FollowService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FollowController {

    private final FollowService service;

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/follow/request")
    public ResponseEntity<Void> requestFollow(@RequestBody FollowRequest request) throws Exception {

        service.request(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/follow/accept/{request_id}")
    public ResponseEntity<Void> acceptFollow(@PathVariable("request_id") Long requestId) throws Exception {

        service.accept(requestId);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/follow/reject/{request_id}")
    public ResponseEntity<Void> rejectFollow(@PathVariable("request_id") Long requestId) throws Exception {

        service.reject(requestId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/follow/cancel/{request_id}")
    public ResponseEntity<Void> cancelFollow(@PathVariable("request_id") Long requestId) throws Exception {

        service.cancel(requestId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/follow/delete")
    public ResponseEntity<Void> deleteFollow(@RequestBody FollowRequest request) throws Exception {

        service.delete(request);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
