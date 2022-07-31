package com.jjbacsa.jjbacsabackend.scrap.repository;

import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapRepository extends JpaRepository<ScrapEntity, Long> {

    // Todo: 페이지네이션
    List<ScrapEntity> findAllByUserAndDirectory(UserEntity user, ScrapDirectoryEntity directory);

    // Todo: 페이지네이션
    List<ScrapEntity> findAllByUserAndDirectoryIsNull(UserEntity user);
}