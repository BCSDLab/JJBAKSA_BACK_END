package com.jjbacsa.jjbacsabackend.follow.repository;

import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DslFollowRepository {

    Page<FollowEntity> findAllByUserWithCursor(UserEntity user, String cursor, Pageable pageable);
}
