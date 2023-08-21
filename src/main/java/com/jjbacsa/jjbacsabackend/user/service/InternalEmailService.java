package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

public interface InternalEmailService {
    void sendAuthEmailCode(UserEntity user) throws Exception;

    void sendAuthEmailLink(UserEntity user, Token token) throws Exception;

    Boolean codeCertification(String email, String code) throws Exception;

    Boolean linkCertification(String email) throws Exception;
}
