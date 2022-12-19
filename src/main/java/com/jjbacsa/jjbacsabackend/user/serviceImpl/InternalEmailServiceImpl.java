package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.user.entity.AuthEmailEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.AuthEmailRepository;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.InternalEmailService;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import com.jjbacsa.jjbacsabackend.util.RedisUtil;
import com.jjbacsa.jjbacsabackend.util.SesSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.util.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class InternalEmailServiceImpl implements InternalEmailService {
    @Value("${context-path}")
    private String contextPath;

    private final SesSender sesSender;
    private final UserRepository userRepository;
    private final AuthEmailRepository authEmailRepository;
    private final OAuthInfoRepository oAuthInfoRepository;
    private final InternalUserService userService;
    private final TemplateEngine templateEngine;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Override
    public void sendAuthEmailCode(String email) throws Exception {

        UserEntity user = userService.getUserByEmail(email);

        if(oAuthInfoRepository.findByUserId(user.getId()).isPresent()) {
            throw new RequestInputException(ErrorMessage.SOCIAL_ACCOUNT_EXCEPTION);
        }

        if(isEmailSentNumExceed(user.getId())) {
            throw new RequestInputException(ErrorMessage.EMAIL_SEND_EXCEED_EXCEPTION);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(System.currentTimeMillis()));
        calendar.add(Calendar.SECOND, 5 * 60);

        String secret = getRandomNumber();

        if(userRepository.findByEmail(email).isPresent()) {
            AuthEmailEntity authEmail = AuthEmailEntity.builder()
                    .secret(secret)
                    .expiredAt(new Timestamp(calendar.getTimeInMillis()))
                    .user(user)
                    .build();

            // 이전 이메일들 삭제
            authEmailRepository.deletePastEmail(user.getId());
            authEmailRepository.save(authEmail);
        }

        sesSender.sendMail(email, "쩝쩝박사 서비스 인증 메일입니다.", secret);
    }

    @Override
    public void sendAuthEmailLink(String email) throws Exception {

        UserEntity user = userService.getUserByEmail(email);

        if(oAuthInfoRepository.findByUserId(user.getId()).isPresent()) {
            throw new RequestInputException(ErrorMessage.SOCIAL_ACCOUNT_EXCEPTION);
        }

        if(isEmailSentNumExceed(user.getId())) {
            throw new RequestInputException(ErrorMessage.EMAIL_SEND_EXCEED_EXCEPTION);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(System.currentTimeMillis()));
        calendar.add(Calendar.SECOND, 60 * 60);

        String existToken = redisUtil.getStringValue(String.valueOf(user.getId()));

        if (existToken == null) {
            existToken = jwtUtil.generateToken(user.getId(), TokenType.REFRESH, user.getUserType().getUserType());
            redisUtil.setToken(String.valueOf(user.getId()), existToken);
        }

        String accessToken = jwtUtil.generateToken(user.getId(), TokenType.ACCESS, user.getUserType().getUserType());

        Map<String, Object> model = new HashMap<>();
        model.put("accessToken", accessToken);
        model.put("refreshToken", existToken);
        model.put("contextPath", contextPath);

        Context context = new Context(Locale.KOREA, model);
        String text = templateEngine.process("register_authenticate", context);

        if(userRepository.findByEmail(email).isPresent()) {
            AuthEmailEntity authEmail = AuthEmailEntity.builder()
                    .secret("secret")
                    .expiredAt(new Timestamp(calendar.getTimeInMillis()))
                    .user(user)
                    .build();

            // 이전 이메일들 삭제
            authEmailRepository.deletePastEmail(user.getId());
            authEmailRepository.save(authEmail);
        }

        sesSender.sendMail(email, "쩝쩝박사 서비스 인증 메일입니다.", text);
    }

    @Override
    public Boolean codeCertification(String email, String code) throws Exception {

        UserEntity user = userService.getUserByEmail(email);

        AuthEmailEntity authEmail = authEmailRepository.findAuthEmailEntityByUserIdAndIsDeleted(user.getId(), 0);

        if(authEmail.getExpiredAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new RequestInputException(ErrorMessage.EMAIL_EXPIRED_EXCEPTION);
        }

        if(!authEmail.getSecret().equals(code)) {
            throw new RequestInputException(ErrorMessage.EMAIL_CODE_FAIL_EXCEPTION);
        }

        return true;
    }

    // 4글자 난수 생성
    private String getRandomNumber() {
        StringBuilder secret = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            secret.append(random.nextInt(9));
        }

        return secret.toString();
    }

    // 이메일 전송 횟수 : 10분에 최대 5개
    private boolean isEmailSentNumExceed(Long userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, -601);
        Timestamp start = new Timestamp(calendar.getTimeInMillis());

        Long count = authEmailRepository.getEmailCount(start, userId);

        return count > 5;
    }


}
