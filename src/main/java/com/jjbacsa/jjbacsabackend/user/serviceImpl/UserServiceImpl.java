package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.FollowedType;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.google.service.InternalGoogleService;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.service.InternalReviewService;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.service.InternalReviewImageService;
import com.jjbacsa.jjbacsabackend.user.dto.EmailRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserModifyRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponseWithFollowedType;
import com.jjbacsa.jjbacsabackend.user.dto.WithdrawReasonResponse;
import com.jjbacsa.jjbacsabackend.user.dto.WithdrawRequest;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.entity.WithdrawReasonEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import com.jjbacsa.jjbacsabackend.user.repository.UserCountRepository;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.repository.WithdrawReasonRepository;
import com.jjbacsa.jjbacsabackend.user.service.InternalEmailService;
import com.jjbacsa.jjbacsabackend.user.service.InternalProfileService;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import com.jjbacsa.jjbacsabackend.util.AuthLinkUtil;
import com.jjbacsa.jjbacsabackend.util.ImageUtil;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import com.jjbacsa.jjbacsabackend.util.RedisUtil;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final InternalUserService userService;
    private final InternalFollowService followService;
    private final InternalProfileService profileService;
    private final InternalEmailService emailService;
    private final InternalReviewService reviewService;
    private final InternalReviewImageService reviewImageService;
    private final InternalGoogleService shopService;
    private final UserRepository userRepository;
    private final UserCountRepository userCountRepository;
    private final WithdrawReasonRepository withdrawReasonRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final ImageUtil imageUtil;
    private final AuthLinkUtil authLinkUtil;
    private final OAuthInfoRepository oAuthInfoRepository;

    @Override
    @Transactional
    public UserResponse register(UserRequest request) throws Exception {
        validateExistAccount(request.getAccount());
        validateExistEmail(request.getEmail());

        request.setNickname(UUID.randomUUID().toString());

        UserEntity user = UserMapper.INSTANCE.toUserEntity(request).toBuilder()
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(UserType.NORMAL)
                .build();

        userRepository.save(user);
        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Transactional
    @Override
    public URI authEmail(String accessToken, String refreshToken) throws Exception {
        jwtUtil.isValid("Bearer " + accessToken, TokenType.ACCESS);

        Long id = Long.parseLong(String.valueOf(jwtUtil.getPayloadsFromJwt(accessToken).get("id")));

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION));

        emailService.linkCertification(user.getEmail());

        user.setAuthEmail(true);

        return authLinkUtil.getAuthLink(accessToken, refreshToken);
    }

    @Override
    public String checkDuplicateAccount(String account) throws Exception {
        validateExistAccount(account);
        return "OK";
    }

    @Override
    @Transactional
    public Token login(UserRequest request) throws Exception {
        UserEntity user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new RequestInputException(ErrorMessage.LOGIN_FAIL_EXCEPTION));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RequestInputException(ErrorMessage.LOGIN_FAIL_EXCEPTION);
        }

        if (!user.isAuthEmail()) {
            throw new RequestInputException(ErrorMessage.INVALID_AUTHENTICATE_EMAIL);
        }

        String existToken = redisUtil.getStringValue(String.valueOf(user.getId()));

        if (existToken == null) {
            existToken = jwtUtil.generateToken(user.getId(), TokenType.REFRESH, user.getUserType().getUserType());
            redisUtil.setToken(String.valueOf(user.getId()), existToken);
        }

        Token token = new Token(
                jwtUtil.generateToken(user.getId(), TokenType.ACCESS, user.getUserType().getUserType()),
                existToken);

        userRepository.updateLastLoggedAt(user.getId(), new Date());

        return token;
    }

    @Override
    public void logout() throws Exception {
        //TODO : 로그아웃 로직 추가
    }

    @Override
    @Transactional
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
        if (existToken == null || !existToken.equals(token.substring(JwtUtil.BEARER_LENGTH))) {
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
        }

        userRepository.updateLastLoggedAt(user.getId(), new Date());

        return new Token(
                jwtUtil.generateToken(user.getId(), TokenType.ACCESS, user.getUserType().getUserType()),
                existToken);
    }

    @Override
    public UserResponse getLoginUser() throws Exception {
        return UserMapper.INSTANCE.toUserResponse(userService.getLoginUser());
    }

    @Override
    public Page<UserResponseWithFollowedType> searchUsers(String keyword, Integer pageSize, Long cursor)
            throws Exception {
        Pageable pageable = PageRequest.of(0, pageSize);

        Page<UserEntity> result = userRepository.findAllByUserNameWithCursor(keyword, pageable, cursor);

        UserEntity loginUser = userService.getLoginUser();
        Map<Long, FollowedType> followedTypes = userRepository.getFollowedTypesByUserAndUsers(loginUser,
                result.getContent());
        return result.map(user -> UserMapper.INSTANCE.toUserResponse(user,
                followedTypes.getOrDefault(user.getId(), FollowedType.NONE)));
    }

    @Override
    public UserResponse getAccountInfo(Long id) throws Exception {
        UserEntity user = userRepository.findUserByIdWithCount(id);

        if (user == null) {
            throw new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION);
        }

        return UserMapper.INSTANCE.toUserResponse(user);
    }

    //TODO : 2차 배포 시 파라미터(아이디) 추가 (변경 시 채널에 고지 )
    @Override
    @Transactional
    public UserResponse modifyUser(UserModifyRequest request) throws Exception {
        UserEntity user = userService.getLoginUser();

        if (user.getPassword() == null && request.getPassword() != null) {
            throw new RequestInputException(ErrorMessage.SOCIAL_ACCOUNT_EXCEPTION);
        }

        if (request.getPassword() != null) {
//            emailService.codeCertification(request.getEmail(), code);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }

        userRepository.save(user);
        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    @Transactional
    public void withdraw() throws Exception {
        UserEntity user = userService.getLoginUser();

        if (oAuthInfoRepository.findByUserId(user.getId()).isPresent()) {
            throw new RequestInputException(ErrorMessage.SOCIAL_ACCOUNT_EXCEPTION);
        }

        // follower / follower 신청 내역 전체 삭제
        userCountRepository.updateAllFriendsCountByUser(user);
        followService.deleteFollowWithUser(user);
        followService.deleteFollowRequestWithUser(user);

        // 작성한 리뷰 및 리뷰 내 사진, 별점 삭제
        List<ReviewEntity> reviews = reviewService.findReviewsByWriter(user);

        for (ReviewEntity review : reviews) {

            for (ReviewImageEntity reviewImage : review.getReviewImages()) { // 리뷰 이미지를 버킷에서 삭제
                reviewImageService.delete(reviewImage);
            }
            review.setIsDeleted(1);

            // 리뷰 수, 별점 처리
            Long shopId = review.getShop().getId();
            shopService.addTotalRating(shopId, -review.getRate());
            shopService.decreaseRatingCount(shopId);
        }

        user.getUserCount().setReviewCount(0);
        user.setIsDeleted(1);

        //회원 탈퇴에 따른 리프레시 토큰 삭제
        String existToken = redisUtil.getStringValue(String.valueOf(user.getId()));
        if (existToken != null) {
            redisUtil.deleteValue(String.valueOf(user.getId()));
        }
    }

    @Override
    @Transactional
    public WithdrawReasonResponse createWithdrawReason(WithdrawRequest request) throws Exception {
        UserEntity user = userService.getLoginUser();

        WithdrawReasonEntity reason = WithdrawReasonEntity.builder()
                .user(user)
                .reason(request.getReason())
                .discomfort(request.getDiscomfort())
                .build();

        withdrawReasonRepository.save(reason);
        return new WithdrawReasonResponse(user.getId(), request);
    }

    @Override
    @Transactional
    public UserResponse modifyProfile(MultipartFile profile) throws Exception {
        UserEntity user = userService.getLoginUser();

        if (user.getProfileImage() != null) {
            profileService.deleteProfileImage(user.getProfileImage());
        }

        ImageEntity image = null;
        if (profile != null) {
            profile = imageUtil.resizing(profile, 500);
            image = profileService.createProfileImage(profile);
        }

        user.setProfileImage(image);
        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    @Transactional
    public void sendAuthEmailCode(String email) throws Exception {
        UserEntity user = getLocalUserByEmail(email);
        emailService.sendAuthEmailCode(user);
    }

    @Override
    @Transactional
    public void sendAuthEmailCode(String account, String email) throws Exception {
        UserEntity user = getLocalUserByEmail(email);

        if (!user.getAccount().equals(account)) {
            throw new RequestInputException(ErrorMessage.INVALID_EMAIL_EXCEPTION);
        }

        emailService.sendAuthEmailCode(user);
    }

    @Override
    @Transactional
    public void sendAuthEmailLink(String email) throws Exception {

        UserEntity user = getLocalUserByEmail(email);
        String existToken = redisUtil.getStringValue(String.valueOf(user.getId()));

        if (existToken == null) {
            existToken = jwtUtil.generateToken(user.getId(), TokenType.REFRESH, user.getUserType().getUserType());
            redisUtil.setToken(String.valueOf(user.getId()), existToken);
        }

        String accessToken = jwtUtil.generateToken(user.getId(), TokenType.ACCESS, user.getUserType().getUserType());

        emailService.sendAuthEmailLink(user, new Token(accessToken, existToken));
    }

    @Override
    public UserResponse findAccount(String email, String code) throws Exception {

        UserEntity user = userService.getLocalUserByEmail(email);

        if (!emailService.codeCertification(email, code)) {
            throw new RequestInputException(ErrorMessage.BAD_AUTHENTICATION_CODE);
        }

        return UserMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    @Transactional
    public String findPassword(EmailRequest request) throws Exception {

        UserEntity user = userService.getLocalUserByEmail(request.getEmail());

        if (!Objects.equals(request.getEmail(), user.getEmail())) {
            throw new RequestInputException(ErrorMessage.INVALID_EMAIL_EXCEPTION);
        }

        if (!emailService.codeCertification(request.getEmail(), request.getCode())) {
            throw new RequestInputException(ErrorMessage.BAD_AUTHENTICATION_CODE);
        }

        return jwtUtil.generateToken(user.getId(), TokenType.ACCESS, user.getUserType().getUserType());
    }

    @Override
    @Transactional
    public UserResponse validatePassword(String password) throws Exception {
        UserEntity user = userService.getLoginUser();

        if (oAuthInfoRepository.findByUserId(user.getId()).isPresent()) {
            throw new RequestInputException(ErrorMessage.SOCIAL_ACCOUNT_EXCEPTION);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RequestInputException(ErrorMessage.PASSWORD_INCORRECT_EXCEPTION);
        }

        return UserMapper.INSTANCE.toUserResponse(user);
    }

    private UserEntity getLocalUserByEmail(String email) throws Exception {

        UserEntity user = userService.getLocalUserByEmail(email);

        if(oAuthInfoRepository.findByUserId(user.getId()).isPresent()) {
            throw new RequestInputException(ErrorMessage.SOCIAL_ACCOUNT_EXCEPTION);
        }

        return user;
    }

    private void validateExistAccount(String account) {
        if (userRepository.existsByAccount(account) > 0) {
            throw new RequestInputException(ErrorMessage.ALREADY_EXISTS_ACCOUNT);
        }
    }

    private void validateExistEmail(String email) {
        if (userRepository.existsByEmailAndPasswordIsNotNull(email)) {
            throw new RequestInputException(ErrorMessage.ALREADY_EXISTS_EMAIL);
        }
    }

    private void validateUserInfo(String account, String email) {
        UserEntity user = userService.getUserByAccount(account);

        if (!Objects.equals(user.getEmail(), email)) {
            throw new RequestInputException(ErrorMessage.INVALID_EMAIL_EXCEPTION);
        }
    }

}
