package com.jjbacsa.jjbacsabackend.user.controller;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @ApiOperation(
            value = "회원가입",
            notes = "회원가입을 진행합니다.\n" +
                    "필요한 필드 : account, password, email")
    @PostMapping(value = "/user")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "아이디 중복 확인",
            notes = "아이디 중복을 확인합니다.")
    @GetMapping(value = "/user/exists")
    public ResponseEntity<String> checkDuplicateAccount(@RequestParam String account) throws Exception {
        return new ResponseEntity<>(userService.checkDuplicateAccount(account), HttpStatus.OK);
    }

    @ApiOperation(
            value = "로그인",
            notes = "로그인을 진행합니다.\n" +
                    "필요한필드 : account, password")
    @PostMapping(value = "/user/login")
    public ResponseEntity<Token> login(@RequestBody UserRequest request, HttpServletResponse httpResponse) throws Exception{
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @ApiOperation(
            value = "본인 정보 확인",
            notes = "로그인 유저 정보 확인(타 기능용 메소드입니다. 작성시 유의바랍니다.)",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/user/me")
    public ResponseEntity<UserResponse> getMe() throws Exception{
        return new ResponseEntity<>(userService.getLoginUser(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "로그아웃",
            notes = "로그아웃합니다.",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @DeleteMapping(value = "/user/logout")
    public ResponseEntity<String> logout() throws Exception{
        userService.logout();
        return new ResponseEntity<>("success", HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "토큰 재발급",
            notes = "accessToken과 refreshToken을 재발급합니다.",
            authorizations = @Authorization(value = "Bearer + refreshToken"))
    @GetMapping("/user/refresh")
    public ResponseEntity<Token> refresh() throws Exception{
        return new ResponseEntity<>(userService.refresh(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "유저 목록 검색",
            notes = "keyword가 포함된 유저 닉네임 검색"
    )
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(required = false) String cursor,
            @PageableDefault(size = 20) Pageable pageable) throws Exception{
        System.out.println(keyword);
        return new ResponseEntity<>(userService.searchUsers(keyword, cursor, pageable), HttpStatus.OK);
    }
}
