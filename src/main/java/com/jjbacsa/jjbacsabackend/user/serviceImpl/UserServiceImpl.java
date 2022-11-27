package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.user.dto.EmailRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import com.jjbacsa.jjbacsabackend.user.repository.UserCountRepository;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.InternalProfileService;
import com.jjbacsa.jjbacsabackend.user.service.InternalEmailService;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import com.jjbacsa.jjbacsabackend.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final InternalUserService userService;
    private final InternalFollowService followService;
    private final InternalProfileService profileService;
    private final InternalEmailService emailService;
    private final UserRepository userRepository;
    private final UserCountRepository userCountRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final OAuthInfoRepository oAuthInfoRepository;

    //TODO : OAuth별 작동
    //TODO : 이메일 인증 추가 시 인증 코드 파라미터 추가 (변경 시 채널에 고지 )
    @Override
    @Transactional
    public UserResponse register(UserRequest request) throws Exception {
        //TODO : 이메일 인증 확인 절차 추가
//        emailService.codeCertification(request.getEmail(), code);

        //TODO : Default Profile 등록하기
        existAccount(request.getAccount());
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
        existAccount(account);
        return "OK";
    }

    @Override
    public Token login(UserRequest request) throws Exception {
        UserEntity user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RequestInputException(ErrorMessage.INVALID_ACCESS);
        }

        String existToken = redisUtil.getStringValue(String.valueOf(user.getId()));

        if (existToken == null) {
            existToken = jwtUtil.generateToken(user.getId(), TokenType.REFRESH, user.getUserType().getUserType());
            redisUtil.setToken(String.valueOf(user.getId()), existToken);
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

        String existToken = redisUtil.getStringValue(String.valueOf(user.getId()));

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
    public Page<UserResponse> searchUsers(String keyword, Integer pageSize, Long cursor) throws Exception {
        Pageable pageable = PageRequest.of(0, pageSize);

        Page<UserResponse> result = userRepository.findAllByUserNameWithCursor(keyword, pageable, cursor)
                .map(UserMapper.INSTANCE::toUserResponse);

        return result;
    }

    @Override
    public UserResponse getAccountInfo(Long id) throws Exception {
        UserEntity user = userRepository.findUserByIdWithCount(id);

        if (user == null) throw new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION);

        return UserMapper.INSTANCE.toUserResponse(user);
    }

    //TODO : Email 인증 추가 완료 시 파라미터 추가 (변경 시 채널에 고지 )
    @Override
    @Transactional
    public UserResponse modifyUser(UserRequest request) throws Exception {
        UserEntity user = userService.getLoginUser();

        if (request.getPassword() != null) {
//            emailService.codeCertification(request.getEmail(), code);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getNickname() != null)
            user.setNickname(request.getNickname());

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

        //회원 탈퇴에 따른 리프레시 토큰 삭제
        String existToken = redisUtil.getStringValue(String.valueOf(user.getId()));
        if (existToken != null) redisUtil.deleteValue(String.valueOf(user.getId()));
    }

    @Override
    @Transactional
    public UserResponse modifyProfile(MultipartFile profile) throws Exception {
        UserEntity user = userService.getLoginUser();
        if(user.getProfileImage() != null)
            profileService.deleteProfileImage(user.getProfileImage());

        ImageEntity image = null;
        if (profile != null)
            image = profileService.createProfileImage(profile);

        user.setProfileImage(image);
        return UserMapper.INSTANCE.toUserResponse(user);
    }

    //TODO : 마스킹 필요하면 마스킹해서 보내줄 것
    @Override
    @Transactional
    public void sendAuthEmail(String email) throws Exception{
        emailService.sendAuthEmail(email);
    }

    @Override
    public UserResponse findAccount(String email, String code) throws Exception {

        UserEntity user = userService.getUserByEmail(email);

        if(oAuthInfoRepository.findByUserId(user.getId()).isPresent()) {
            throw new RequestInputException(ErrorMessage.SOCIAL_ACCOUNT_EXCEPTION);
        }

        if (!emailService.codeCertification(email, code))
            throw new RequestInputException(ErrorMessage.BAD_AUTHENTICATION_CODE);

        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse findPassword(EmailRequest request) throws Exception{

        if(!userRepository.existsByAccount(request.getAccount())) {
            throw new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION);
        }

        UserEntity user = userService.getUserByEmail(request.getEmail());

        if(oAuthInfoRepository.findByUserId(user.getId()).isPresent()) {
            throw new RequestInputException(ErrorMessage.SOCIAL_ACCOUNT_EXCEPTION);
        }

        if (!emailService.codeCertification(request.getEmail(), request.getCode()))
            throw new RequestInputException(ErrorMessage.BAD_AUTHENTICATION_CODE);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return UserMapper.INSTANCE.toUserResponse(user);
    }

    private boolean existAccount(String account) {
        if (userRepository.existsByAccount(account)) {
            throw new RequestInputException(ErrorMessage.ALREADY_EXISTS_ACCOUNT);
        }
        return true;
    }
}
