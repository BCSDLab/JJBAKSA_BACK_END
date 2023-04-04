package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.user.entity.AuthEmailEntity;

public interface InternalEmailService {
    void sendAuthEmailCode(String email) throws Exception;

    void sendAuthEmailLink(String email) throws Exception;

    Boolean codeCertification(String email, String code) throws Exception;

    Boolean linkCertification(String email) throws Exception;

    AuthEmailEntity getEmailByUserIdAndIsDeleted(Long userId, int isDeleted) throws Exception;
}
