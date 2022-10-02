package com.jjbacsa.jjbacsabackend.follow.service;

import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

public interface InternalFollowService {

    FollowRequestEntity getFollowRequestById(Long id) throws RequestInputException;

    boolean existsByUserAndFollower(UserEntity user, UserEntity follower);

}
