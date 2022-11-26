package com.jjbacsa.jjbacsabackend.user.controller;

import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.service.RootUserService;
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

    @PostMapping(value = "/root/admin")
    public ResponseEntity<UserResponse> adminAuthority(@RequestParam String account){
        return new ResponseEntity<>(rootUserService.adminAuthority(account), HttpStatus.OK);
    }
    @PostMapping(value = "/root/normal")
    public ResponseEntity<UserResponse> normalAuthority(@RequestParam String account){
        return new ResponseEntity<>(rootUserService.normalAuthority(account), HttpStatus.OK);
    }
}
