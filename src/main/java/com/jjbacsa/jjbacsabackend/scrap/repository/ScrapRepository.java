package com.jjbacsa.jjbacsabackend.scrap.repository;

import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import com.jjbacsa.jjbacsabackend.scrap.repository.dsl.DslScrapRepository;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<ScrapEntity, Long>, DslScrapRepository {

    boolean existsByUserAndShop(UserEntity user, GoogleShopEntity shop);

    List<ScrapEntity> findAllByUser(UserEntity user);

    Optional<ScrapEntity> findByUserAndShop(UserEntity user, GoogleShopEntity shop);
}