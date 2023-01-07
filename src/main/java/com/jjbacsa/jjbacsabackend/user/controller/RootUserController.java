package com.jjbacsa.jjbacsabackend.user.controller;

import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.service.RootUserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('ROOT')")
@RequiredArgsConstructor
@RestController
public class RootUserController {
    private final RootUserService rootUserService;

    @ApiOperation(
            value = "ADMIN 권한 부여",
            notes = "사용자에게 ADMIN 권한을 부여합니다.\n\n" +
                    "account : 유저 계정(1~20글자의 영문자 및 숫자),\n\n")
    @PostMapping(value = "/root/admin")
    public ResponseEntity<UserResponse> adminAuthority(@RequestParam String account){
        return new ResponseEntity<>(rootUserService.adminAuthority(account), HttpStatus.OK);
    }
    @ApiOperation(
            value = "NORMAL 권한 부여",
            notes = "사용자에게 NORMAL 권한을 부여합니다.\n\n" +
                    "account : 유저 계정(1~20글자의 영문자 및 숫자),\n\n")
    @PostMapping(value = "/root/normal")
    public ResponseEntity<UserResponse> normalAuthority(@RequestParam String account){
        return new ResponseEntity<>(rootUserService.normalAuthority(account), HttpStatus.OK);
    }
}
