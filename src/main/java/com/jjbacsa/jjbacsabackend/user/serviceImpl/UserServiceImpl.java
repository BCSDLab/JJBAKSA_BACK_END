package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    //TODO : OAuth별 작동
    @Override
    @Transactional
    public UserResponse signUp(UserRequest request) throws Exception {
        if(userRepository.existsByAccount(request.getAccount())) throw new Exception();
        //TODO : 이메일 인증 확인 절차 추가

        //TODO : Default Profile 등록하기
        UserEntity user = UserEntity.builder()
                .account(request.getAccount())
                .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .userType(UserType.NORMAL)
                .oAuthType(OAuthType.NONE)
                .build();

        userRepository.save(user);
        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    @Transactional
    public Map<String, String> login(UserRequest request) throws Exception{
        UserEntity user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new Exception("User Not Founded"));

        if(!BCrypt.checkpw(request.getPassword(), user.getPassword()))
            throw new Exception("Not Invalid Access");

        //TODO : 토큰 전달 보안 강화
        Map<String, String> token = new HashMap<>();
        token.put("access_token", jwtUtil.generateToken(user.getId()));
        return token;
    }
}
