package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    //TODO : OAuth별 작동
    @Override
    @Transactional
    public UserResponse register(UserRequest request) throws Exception {
        if(userRepository.existsByAccount(request.getAccount())){
            throw new Exception();
        }
        //TODO : 이메일 인증 확인 절차 추가

        //TODO : Default Profile 등록하기
        UserEntity user = UserMapper.INSTANCE.toUserEntity(request).toBuilder()
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(UserType.NORMAL)
                .build();

        userRepository.save(user);
        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    public Token login(UserRequest request, HttpServletResponse httpResponse) throws Exception{
        UserEntity user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new Exception("User Not Founded"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception("User Not Founded");
        }

        return getTokens(user, httpResponse);
    }

    @Override
    public UserResponse getLoginUser() throws Exception{
        UserEntity user = ((CustomUserDetails)SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUser();

        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    public void logout(HttpServletResponse httpResponse) throws Exception{
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        httpResponse.addCookie(cookie);
    }

    @Override
    public Token refresh(String token, HttpServletResponse httpResponse) throws Exception{
        //HttpOnly 쿠키는 공백을 담을 수 없고 클라이언트 접근 불가
        String bearerToken = "Bearer " + token;
        jwtUtil.isValid(bearerToken, TokenType.REFRESH);
        String account = (String)jwtUtil.getPayloadsFromJwt(bearerToken).get("account");

        UserEntity user = userRepository.findByAccount(account)
                .orElseThrow(() -> new Exception("User Not Founded"));;

        return getTokens(user, httpResponse);
    }

    //login, refresh 중복 로직
    private Token getTokens(UserEntity user, HttpServletResponse httpResponse){
        Cookie cookie = new Cookie("refresh",
                jwtUtil.generateToken(user.getAccount(), TokenType.REFRESH));
        cookie.setMaxAge(14*24*60*60);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true); //local 테스트시 https 미사용으로 Secure 미부여
        cookie.setPath("/");

        httpResponse.addCookie(cookie);

        return new Token(jwtUtil.generateToken(user.getAccount(), TokenType.ACCESS));
    }
}
