package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.user.service.EmailService;
import com.jjbacsa.jjbacsabackend.util.RedisUtil;
import com.jjbacsa.jjbacsabackend.util.SesSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final SesSender sesSender;
    private final RedisUtil redisUtil;

    public void sendAuthEmail(String email) {
        String secret = getRandomNumber();

        //TODO : 이메일 인증 제약 기획 완료 시 반영
        //TODO : RDB 저장으로 수정
        String emailAccount = email.split("@")[0];
        redisUtil.setToken(emailAccount, secret);

        sesSender.sendMail("no-reply@jjbaksa.com", email, "쩝쩝박사 서비스 인증 메일입니다.", secret);
    }

    //4글자 난수 생성
    private String getRandomNumber() {
        StringBuilder secret = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            secret.append(random.nextInt(9));
        }

        return secret.toString();
    }
}
