package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    //TODO : OAuth별 작동
    @Override
    @Transactional
    public UserResponse signUp(UserRequest request) throws Exception {
        if(userRepository.existsByAccount(request.getAccount())) throw new Exception();

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

}
