package com.jjbacsa.jjbacsabackend.rating.repository;

import com.jjbacsa.jjbacsabackend.rating.entity.RatingEntity;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {

    Optional<RatingEntity> findByUserAndShop(UserEntity user, ShopEntity shop);

    boolean existsByUserAndShop(UserEntity user, ShopEntity shop);
}