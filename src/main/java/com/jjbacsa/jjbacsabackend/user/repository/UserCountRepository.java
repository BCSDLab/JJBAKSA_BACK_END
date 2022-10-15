package com.jjbacsa.jjbacsabackend.user.repository;

import com.jjbacsa.jjbacsabackend.user.entity.UserCount;
import com.jjbacsa.jjbacsabackend.user.repository.querydsl.DslUserCountRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCountRepository extends JpaRepository<UserCount, Long>, DslUserCountRepository {
}
