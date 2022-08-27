package com.jjbacsa.jjbacsabackend.user.controller;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/user")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
    }

    @PostMapping(value = "/user/login")
    public ResponseEntity<Token> login(@RequestBody UserRequest request) throws Exception{
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    //@Auth
    @ApiOperation( value = "",notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/user/me")
    public ResponseEntity<String> getMe() throws Exception{
        //return new ResponseEntity<>(userService.getLoginUser(), HttpStatus.OK);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
