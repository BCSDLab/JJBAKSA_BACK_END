package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.user.dto.request.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserResponse;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserResponseWithFollowedType;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import com.jjbacsa.jjbacsabackend.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@Transactional
public class UserServiceTest {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private UserEntity user;
    private UserRequest loginRequest;

    @BeforeEach
    void setup() throws Exception {
        UserEntity testUser = UserEntity.builder()
                .account("test1")
                .password("password")
                .email("test2@google.com")
                .nickname("test1")
                .userType(UserType.NORMAL)
                .build();
        user = userRepository.save(testUser);
        testLogin(user);

        loginRequest = new UserRequest("test2", "test2!", "test2@google.com", "test1");
    }

    @DisplayName("회원 가입")
    @Test
    void register() throws Exception {
        UserRequest testRequest = new UserRequest("", "", "", "");

        UserResponse response = userService.register(loginRequest);

        UserEntity testUser = userRepository.findByAccount(loginRequest.getAccount())
                .orElseThrow(() -> new Exception("Not Found"));

        assertEquals(UserMapper.INSTANCE.toUserResponse(testUser).getId(), response.getId());
    }

    @DisplayName("로그인")
    @Test
    void login() throws Exception {
        UserResponse userResponse = userService.register(loginRequest);

        UserRequest loginInfo = new UserRequest("", loginRequest.getPassword(), "", "");
        //아이디 틀린 경우
        assertThrows(RequestInputException.class, () ->
                userService.login(loginInfo));

        loginInfo.setAccount(loginRequest.getAccount());
        loginInfo.setPassword("");
        //비밀번호 틀린 경우
        assertThrows(RequestInputException.class, () ->
                userService.login(loginInfo));

        loginInfo.setPassword(loginRequest.getPassword());
        Token token = userService.login(loginInfo);

        //토큰 타입 불일치
        assertThrows(RequestInputException.class, () ->
                jwtUtil.isValid("Bearer " + token.getAccessToken(), TokenType.REFRESH));
        assertThrows(RequestInputException.class, () ->
                jwtUtil.isValid("Bearer " + token.getRefreshToken(), TokenType.ACCESS));

        assertEquals(jwtUtil.isValid("Bearer " + token.getAccessToken(), TokenType.ACCESS), true);
        assertEquals(
                jwtUtil.getPayloadsFromJwt(token.getAccessToken()).get("id").toString(),
                userResponse.getId().toString());
        assertEquals(jwtUtil.isValid("Bearer " + token.getRefreshToken(), TokenType.REFRESH), true);
    }


    @DisplayName("로그인 유저 정보 확인")
    @Test
    void getLoginUser() throws Exception {
        UserResponse response = userService.getLoginUser();

        assertEquals(user.getId(), response.getId());
    }

    @DisplayName("유저 수정")
    @Test
    void modifyUser() throws Exception {
        UserRequest request = new UserRequest();
        request.setNickname("Test");

        userService.modifyUser(request);
        assertEquals(userService.getLoginUser().getNickname(), "Test");
    }

    @DisplayName("유저 리스트 검색")
    @Test
    void searchUsers() throws Exception {
        Page<UserResponseWithFollowedType> result = userService
                .searchUsers("NoSearchName", 10, 0L);
        assertTrue(result.isEmpty());

        UserRequest request = new UserRequest();
        request.setNickname("SearchName");
        userService.modifyUser(request);

        result = userService
                .searchUsers("SearchName", 10, 0L);
        assertEquals(result.getContent().get(0).getId(),
                userService.getLoginUser().getId());
    }

    @DisplayName("유저 검색")
    @Test
    void getAccountInfo() throws Exception {
        assertThrows(RequestInputException.class,
                () -> userService.getAccountInfo(999999L));

        assertEquals(userService.getAccountInfo(user.getId()).getId(),
                userService.getLoginUser().getId());
    }

    @DisplayName("회원 탈퇴")
    @Test
    void withdraw() throws Exception {
        userService.withdraw();
        assertThrows(RequestInputException.class,
                () -> userService.getAccountInfo(userService.getLoginUser().getId()));

        assertEquals(redisUtil.getStringValue(String.valueOf(user.getId())), null);
    }

    private void testLogin(UserEntity user) throws Exception {
        UserDetails userDetails = new CustomUserDetails(user.getId());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
