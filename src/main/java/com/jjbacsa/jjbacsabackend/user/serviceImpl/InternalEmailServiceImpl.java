package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.user.service.InternalEmailService;
import com.jjbacsa.jjbacsabackend.util.RedisUtil;
import com.jjbacsa.jjbacsabackend.util.SesSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class InternalEmailServiceImpl implements InternalEmailService {
    private final SesSender sesSender;
    private final RedisUtil redisUtil;

    public void sendAuthEmail(String email) throws Exception {
        String secret = getRandomNumber();

        //TODO : 이메일 인증 제약 기획 완료 시 반영
        //TODO : RDB 저장으로 수정
        String emailAccount = email.split("@")[0];
        redisUtil.setToken(emailAccount, secret);

        sesSender.sendMail(email, "쩝쩝박사 서비스 인증 메일입니다.", secret);
    }

    public void sendAccountEmail(String email, String account) throws Exception {
        sesSender.sendMail(email, "쩝쩝박사 아이디 찾기 메일입니다.", account);
    }

    //TODO : 용도에 따른 인증 필요한지 판단 필요
    @Override
    public Boolean codeCertification(String email, String code) throws Exception {
        //TODO : 이메일 인증 제약 기획 완료 시 반영
        //TODO : RDB 저장으로 수정
        String existCode = redisUtil.getStringValue(email.split("@")[0]);
        return existCode.equals(code);
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
