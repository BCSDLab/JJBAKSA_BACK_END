package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

public interface InternalUserService {

    UserEntity getUserById(Long userId);

    void increaseReviewCount(Long userId);

    void increaseScrapCount(Long userId);

    void increaseFriendCount(Long userId);

    void decreaseReviewCount(Long userId);

    void decreaseScrapCount(Long userId);

    void decreaseFriendCount(Long userId);
}
