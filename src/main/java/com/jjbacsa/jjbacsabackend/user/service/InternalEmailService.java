package com.jjbacsa.jjbacsabackend.user.service;

public interface InternalEmailService {
    void sendAuthEmail(String email) throws Exception;

    void sendAccountEmail(String email, String account) throws Exception;

    Boolean codeCertification(String email, String code) throws Exception;
}
