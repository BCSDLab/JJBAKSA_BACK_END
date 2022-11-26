package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.RootUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RootUserServiceImpl implements RootUserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse adminAuthority(String account) {
        UserEntity user = findUserByAccount(account);
        user.modifyUserRole(UserType.ADMIN);
        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    public UserResponse normalAuthority(String account) {
        UserEntity user = findUserByAccount(account);
        user.modifyUserRole(UserType.NORMAL);
        return UserMapper.INSTANCE.toUserResponse(user);
    }
    private UserEntity findUserByAccount(String account){
        return userRepository.findByAccount(account).orElseThrow(
                () -> new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION));
    }
}
