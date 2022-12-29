package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

public interface InternalUserService {

    UserEntity getUserById(Long userId) throws RequestInputException;

    UserEntity getUserByAccount(String account) throws RequestInputException;

    UserEntity getLoginUser() throws Exception;

    UserEntity getUserByEmail(String email) throws RequestInputException;

    UserEntity getLocalUserByEmail(String email) throws Exception;

    void increaseReviewCount(Long userId);

    void increaseScrapCount(Long userId);

    void increaseFriendCount(Long userId);

    void decreaseReviewCount(Long userId);

    void decreaseScrapCount(Long userId);

    void decreaseFriendCount(Long userId);

    void addScrapCount(Long userId, int delta);
}
