package com.jjbacsa.jjbacsabackend.follow.service;

import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

import java.util.List;

public interface InternalFollowService {

    FollowRequestEntity getFollowRequestById(Long id) throws RequestInputException;

    boolean existsByUserAndFollower(UserEntity user, UserEntity follower);

    Long deleteFollowWithUser(UserEntity user);

    Long deleteFollowRequestWithUser(UserEntity user);

    List<UserEntity> getFollowers() throws Exception;
}
