package com.jjbacsa.jjbacsabackend.user.service;

public interface InternalEmailService {
    void sendAuthEmailCode(String email) throws Exception;

    void sendAuthEmailLink(String email) throws Exception;

    Boolean codeCertification(String email, String code) throws Exception;
}
