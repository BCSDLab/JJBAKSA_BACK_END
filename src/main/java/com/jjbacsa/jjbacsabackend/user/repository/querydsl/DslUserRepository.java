package com.jjbacsa.jjbacsabackend.user.repository.querydsl;

import com.jjbacsa.jjbacsabackend.etc.enums.FollowedType;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DslUserRepository {
    Page<UserEntity> findAllByUserNameWithCursor(String keyword, Pageable pageable, Long cursor);
    Map<Long, FollowedType> getFollowedTypesByUserAndUsers(UserEntity user, List<UserEntity> users);
    UserEntity findUserByIdWithCount(Long id);
    List<UserEntity> findAllUserByIdAndFollowWithCount(Long id);
}
