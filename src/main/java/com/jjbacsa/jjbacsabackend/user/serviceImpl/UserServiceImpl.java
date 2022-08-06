package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
    public UserResponse register(UserRequest request) throws Exception {
        if(userRepository.existsByAccount(request.getAccount())){
            throw new Exception();
        }
        //TODO : 이메일 인증 확인 절차 추가

        //TODO : Default Profile 등록하기
        UserEntity user = UserMapper.INSTANCE.toUserEntity(request).toBuilder()
                .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                .userType(UserType.NORMAL)
                .oAuthType(OAuthType.NONE)
                .build();

        userRepository.save(user);
        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    public Token login(UserRequest request) throws Exception{
        UserEntity user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new Exception("User Not Founded"));

        if(!BCrypt.checkpw(request.getPassword(), user.getPassword())){
            throw new Exception("User Not Founded");
        }

        //TODO : 토큰 전달 보안 강화
        return new Token(jwtUtil.generateToken(user.getId()));
    }

    @Override
    public UserResponse getLoginUser() throws Exception{
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        Map<String, Object> payloads = jwtUtil.getPayloadsFromJwt(token.substring(7));
        UserEntity user = userRepository.findById(Long.valueOf(String.valueOf(payloads.get("id"))))
                .orElseThrow(() -> new Exception("Token is not valid"));

        return UserMapper.INSTANCE.toUserResponse(user);
    }
}
