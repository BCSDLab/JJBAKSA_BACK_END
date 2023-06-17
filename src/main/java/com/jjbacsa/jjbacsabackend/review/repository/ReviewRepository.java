package com.jjbacsa.jjbacsabackend.review.repository;


import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.repository.querydsl.DslReviewRepository;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long>, DslReviewRepository {

    Page<ReviewEntity> findAllByWriterId(Long writerId, Pageable pageable);

    Page<ReviewEntity> findAllByShopId(Long shopId, Pageable pageable);

    // Todo: 페이지네이션
    List<ReviewEntity> findAllByWriterAndShop(UserEntity writer, ShopEntity shop);

    List<ReviewEntity> findAllByWriterId(Long writerId);
}