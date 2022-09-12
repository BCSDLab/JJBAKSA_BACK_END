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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/user")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
    }

    @PostMapping(value = "/user/login")
    public ResponseEntity<Token> login(@RequestBody UserRequest request, HttpServletResponse httpResponse) throws Exception{
        return new ResponseEntity<>(userService.login(request, httpResponse), HttpStatus.OK);
    }

    //@Auth
    @PreAuthorize("hasRole('NORMAL')")
    @ApiOperation( value = "",notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/user/me")
    public ResponseEntity<UserResponse> getMe() throws Exception{
        return new ResponseEntity<>(userService.getLoginUser(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/user/logout")
    public ResponseEntity<String> logout(HttpServletResponse httpResponse)throws Exception{
        userService.logout(httpResponse);
        return new ResponseEntity<>("success", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/user/refresh")
    public ResponseEntity<Token> refresh(@CookieValue(value="refresh")String token, HttpServletResponse httpResponse) throws Exception{
        return new ResponseEntity<>(userService.refresh(token, httpResponse), HttpStatus.OK);
    }
}
