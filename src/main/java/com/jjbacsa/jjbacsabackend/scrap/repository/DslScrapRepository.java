package com.jjbacsa.jjbacsabackend.scrap.repository;

import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DslScrapRepository {

    Page<ScrapEntity> findAllByUserAndDirectoryWithCursor(UserEntity user, ScrapDirectoryEntity directory, Long cursor, Pageable pageable);

    long deleteAllByDirectory(ScrapDirectoryEntity directory);
}
