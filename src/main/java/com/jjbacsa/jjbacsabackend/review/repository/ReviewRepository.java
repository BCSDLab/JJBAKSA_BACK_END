package com.jjbacsa.jjbacsabackend.review.repository;

import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findAllByWriter(UserEntity writer);

    List<ReviewEntity> findAllByShop(ShopEntity shop);

    List<ReviewEntity> findAllByWriterAndShop(UserEntity writer, ShopEntity shop);
}