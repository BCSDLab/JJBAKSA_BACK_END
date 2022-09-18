package com.jjbacsa.jjbacsabackend.user.repository.querydsl;

import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DslUserRepository {
    Page<UserEntity> findAllByUserByNameWithCursor(String keyword, Pageable pageable, Long cursor);
}
