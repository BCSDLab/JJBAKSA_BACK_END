package com.jjbacsa.jjbacsabackend.user.repository.querydsl;

import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

public interface DslUserCountRepository {
    Long updateAllFriendsCountByUser(UserEntity user);
}
