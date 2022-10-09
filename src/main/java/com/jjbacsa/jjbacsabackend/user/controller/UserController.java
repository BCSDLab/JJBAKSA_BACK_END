package com.jjbacsa.jjbacsabackend.user.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @ApiOperation(
            value = "회원가입",
            notes = "회원가입을 진행합니다.\n\n" +
                    "필요한 필드\n\n" +
                    "{\n\n     " +
                    "account : 유저 계정,\n\n     " +
                    "password : 유저 패스워드,\n\n     " +
                    "email : 유저 이메일(차후 인증 추가)\n\n}")
    @PostMapping(value = "/user")
    public ResponseEntity<UserResponse> register(@Validated(ValidationGroups.Create.class) @RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "아이디 중복 확인",
            notes = "아이디 중복을 확인합니다.\n\n")
    @GetMapping(value = "/user/exists")
    public ResponseEntity<String> checkDuplicateAccount(@RequestParam String account) throws Exception {
        return new ResponseEntity<>(userService.checkDuplicateAccount(account), HttpStatus.OK);
    }

    @ApiOperation(
            value = "로그인",
            notes = "로그인을 진행합니다.\n\n" +
                    "필요한 필드\n\n" +
                    "{\n\n     " +
                    "account : 유저 계정,\n\n     " +
                    "password : 유저 패스워드,\n\n" +
                    "}")
    @PostMapping(value = "/user/login")
    public ResponseEntity<Token> login(@Validated(ValidationGroups.Login.class) @RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @ApiOperation(
            value = "본인 정보 확인",
            notes = "로그인 유저 정보 확인\n\n" +
                    "필요 헤더\n\n" +
                    "Authorization : access token\n\n" +
                    "RefreshToken : refresh token",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/user/me")
    public ResponseEntity<UserResponse> getMe() throws Exception {
        return new ResponseEntity<>(userService.getLoginUser(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "토큰 재발급",
            notes = "accessToken과 refreshToken을 재발급합니다.\n\n" +
                    "필요 헤더\n\n" +
                    "Authorization : access token\n\n" +
                    "RefreshToken : refresh token",
            authorizations = @Authorization(value = "Bearer + refreshToken"))
    @GetMapping("/user/refresh")
    public ResponseEntity<Token> refresh() throws Exception {
        return new ResponseEntity<>(userService.refresh(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "유저 목록 검색",
            notes = "keyword가 포함된 유저 닉네임 검색\n\n" +
                    "keyword : 검색에 사용할 문자열 (Not Null)\n\n" +
                    "pageNumber : 검색 결과를 받을 페이지\n\n" +
                    "pageSize : 한 번에 출력할 결과 갯수\n\n" +
                    "cursor : 마지막으로 조회한 유저의 id\n\n" +
                    "keyword 제외하고 모두 null을 주면 검색 첫페이지가 반환됩니다."
    )
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) Long cursor) throws Exception {
        return new ResponseEntity<>(userService.searchUsers(keyword, pageable, cursor), HttpStatus.OK);
    }

    @ApiOperation(
            value = "유저 검색",
            notes = "id : 검색할 유저의 id"
    )
    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponse> getAccountInfo(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(userService.getAccountInfo(id), HttpStatus.OK);
    }

    @ApiOperation(
            value = "유저 정보 변경",
            notes = "유저 정보 변경\n\n" +
                    "필요 헤더\n\n" +
                    "Authorization : access token\n\n" +
                    "RefreshToken : refresh token\n\n" +
                    "필요한 필드\n\n" +
                    "{\n\n     " +
                    "password : 변경할 유저 패스워드(차후 인증 적용),\n\n     " +
                    "nickname : 변경할 유저 닉네임,\n\n     " +
                    "email : 변경할 유저 계정 (차후 인증 적용),\n\n" +
                    "}",
            authorizations = @Authorization(value = "Bearer + refreshToken"))
    @PreAuthorize("hasRole('NORMAL')")
    @PatchMapping("/user/me")
    public ResponseEntity<UserResponse> modifyUser(@RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.modifyUser(request), HttpStatus.OK);
    }

    @ApiOperation(
            value = "회원 탈퇴",
            notes = "회원 탈퇴\n\n" +
                    "필요 헤더\n\n" +
                    "Authorization : access token\n\n" +
                    "RefreshToken : refresh token\n\n"
    )
    @PreAuthorize(("hasRole('NORMAL')"))
    @DeleteMapping("/user/me")
    public ResponseEntity<Void> withdraw() throws Exception {
        userService.withdraw();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
