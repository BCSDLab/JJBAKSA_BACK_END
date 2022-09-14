package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Transactional
public class UserServiceTest {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final MockMvc mockMvc;

    private UserRequest request;
    private UserRequest loginRequest;

    @BeforeEach
    void setup() throws Exception{
        request = new UserRequest("test1", "test1", "test1@google.com", "test1");
        loginRequest = new UserRequest("test2", "test2", "test2@google.com", "test1");
    }

    @DisplayName("회원 가입")
    @Test
    void register() throws Exception{
        UserResponse response = userService.register(request);

        //중복 요청 검사
        assertThrows(Exception.class, () ->
               userService.register(request));

        UserEntity testUser = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new Exception("Not Found"));

        assertEquals(UserMapper.INSTANCE.toUserResponse(testUser).getId(), response.getId());
    }

    @DisplayName("로그인")
    @Test
    void login() throws Exception{
        userService.register(loginRequest);

        UserRequest loginInfo = new UserRequest("", loginRequest.getPassword(), "", "");
        //아이디 틀린 경우
        assertThrows(Exception.class, () ->
                userService.login(loginInfo));

        loginInfo.setAccount(loginRequest.getAccount());
        loginInfo.setPassword("");
        //비밀번호 틀린 경우
        assertThrows(Exception.class, () ->
                userService.login(loginInfo));

        loginInfo.setPassword(loginRequest.getPassword());
        Token token = userService.login(loginInfo);

        //토큰 타입 불일치
        assertThrows(Exception.class, () ->
                jwtUtil.isValid("Bearer " + token.getAccessToken(), TokenType.REFRESH));
        assertThrows(Exception.class, () ->
                jwtUtil.isValid("Bearer " + token.getRefreshToken(), TokenType.ACCESS));

        assertEquals(jwtUtil.isValid("Bearer " + token.getAccessToken(), TokenType.ACCESS), true);
        assertEquals(
                jwtUtil.getPayloadsFromJwt(token.getAccessToken()).get("account"),
                loginRequest.getAccount());
        assertEquals(jwtUtil.isValid("Bearer " + token.getRefreshToken(), TokenType.REFRESH), true);
        assertEquals(
                jwtUtil.getPayloadsFromJwt(token.getRefreshToken()).get("account"),
                loginRequest.getAccount());
    }

    @DisplayName("토큰 재발급")
    @Test
    void refresh() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        userService.register(loginRequest);

        Token token = userService.login(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/refresh")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getRefreshToken()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$..['accessToken']").exists())
                        .andExpect(jsonPath("$..['refreshToken']").exists());
    }

    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception{
        userService.logout();
    }

    //WithUserDetails에 존재하는 Account 값을 넣으면 됩니다.
    @DisplayName("로그인 유저 정보 확인")
    @Test
    @WithUserDetails(value = "12345")
    void getLoginUser() throws Exception {
        assertEquals(userService.getLoginUser().getAccount(), "12345");
    }
}
