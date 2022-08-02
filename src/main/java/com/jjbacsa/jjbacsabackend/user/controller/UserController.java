package com.jjbacsa.jjbacsabackend.user.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.Auth;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping(value = "/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/sign-up")
    public ResponseEntity<UserResponse> signUp(@RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.signUp(request), HttpStatus.CREATED);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserRequest request) throws Exception{
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @Auth
    @ApiOperation( value = "",notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/me")
    public ResponseEntity<UserResponse> getMe() throws Exception{
        return new ResponseEntity<>(userService.getLoginUser(), HttpStatus.OK);
    }
}
