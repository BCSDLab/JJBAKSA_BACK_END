package com.jjbacsa.jjbacsabackend.inquiry.service;

import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

public interface InternalInquiryService {

    void deleteInquiriesWithUser(UserEntity user) throws Exception;

}
