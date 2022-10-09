package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserCountRepository;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final InternalUserService userService;
    private final InternalFollowService followService;
    private final UserRepository userRepository;
    private final UserCountRepository userCountRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    //TODO : OAuth별 작동
    @Override
    @Transactional
    public UserResponse register(UserRequest request) throws Exception {
        //TODO : 이메일 인증 확인 절차 추가

        //TODO : Default Profile 등록하기
        //TODO : Validation 추가하면서 수정
        request.setNickname(UUID.randomUUID().toString());

        UserEntity user = UserMapper.INSTANCE.toUserEntity(request).toBuilder()
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(UserType.NORMAL)
                .build();

        userRepository.save(user);
        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    public String checkDuplicateAccount(String account) throws Exception {
        if (userRepository.existsByAccount(account)) {
            throw new RequestInputException(ErrorMessage.ALREADY_EXISTS_ACCOUNT);
        }
        return "OK";
    }

    @Override
    public Token login(UserRequest request) throws Exception {
        UserEntity user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RequestInputException(ErrorMessage.INVALID_ACCESS);
        }

        String existToken = redisTemplate.opsForValue().get(user.getAccount());

        if (existToken == null) {
            existToken = jwtUtil.generateToken(user.getId(), TokenType.REFRESH, user.getUserType().getUserType());
            redisTemplate.opsForValue().set(user.getAccount(), existToken, 14, TimeUnit.DAYS);
        }

        Token token = new Token(
                jwtUtil.generateToken(user.getId(), TokenType.ACCESS, user.getUserType().getUserType()),
                existToken);

        return token;
    }

    @Override
    public void logout() throws Exception {
        //TODO : 로그아웃 로직 추가
    }

    @Override
    public Token refresh() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("RefreshToken");

        jwtUtil.isValid(token, TokenType.REFRESH);
        Long id = Long.parseLong(String.valueOf(jwtUtil.getPayloadsFromJwt(token).get("id")));

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION));

        String existToken = redisTemplate.opsForValue().get(user.getAccount());

        //null인 경우에는 다시 로그인 필요
        if (existToken == null || !existToken.equals(token.substring(JwtUtil.BEARER_LENGTH)))
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);

        return new Token(
                jwtUtil.generateToken(user.getId(), TokenType.ACCESS, user.getUserType().getUserType()),
                existToken);
    }

    @Override
    public UserResponse getLoginUser() throws Exception {
        return UserMapper.INSTANCE.toUserResponse(userService.getLoginUser());
    }

    @Override
    public Page<UserResponse> searchUsers(String keyword, Pageable pageable, Long cursor) throws Exception {
        Page<UserResponse> result = userRepository.findAllByUserNameWithCursor(keyword, pageable, cursor)
                .map(UserMapper.INSTANCE::toUserResponse);

        if (result == null) throw new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION);

        return result;
    }

    @Override
    public UserResponse getAccountInfo(Long id) throws Exception {
        UserEntity user = userRepository.findUserByIdWithCount(id);

        if (user == null) throw new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION);

        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    public UserResponse modifyUser(UserRequest request) throws Exception {
        Long id = ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getId();

        UserEntity user = userRepository.getById(id);

        if (request.getPassword() != null)
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getNickname() != null)
            user.setNickname(request.getNickname());
        //TODO : email 변경시 인증된 이메일 확인

        userRepository.save(user);
        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    @Transactional
    public void withdraw() throws Exception {
        UserEntity user = userService.getLoginUser();

        userCountRepository.updateAllFriendsCountByUser(user);
        followService.deleteFollowWithUser(user);

        user.setIsDeleted(1);
        userRepository.save(user);

        //회원 탈퇴에 따른 리프레시 토큰 삭제
        String existToken = redisTemplate.opsForValue().get(user.getAccount());
        if (existToken != null) redisTemplate.delete(user.getAccount());
    }
}
