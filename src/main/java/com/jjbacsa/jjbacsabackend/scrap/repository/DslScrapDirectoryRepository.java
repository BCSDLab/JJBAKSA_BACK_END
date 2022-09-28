package com.jjbacsa.jjbacsabackend.scrap.repository;

import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DslScrapDirectoryRepository {

    Page<ScrapDirectoryEntity> findAllByUserWithCursor(UserEntity user, String cursor, Pageable pageable);
}
