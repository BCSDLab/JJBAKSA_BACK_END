package com.jjbacsa.jjbacsabackend.shop.repository;

import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.repository.querydsl.DslShopRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long>, DslShopRepository {

    Optional<ShopEntity> findByPlaceId(String placeId);

    boolean existsByPlaceId(String placeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sc.totalRating from ShopCount sc " +
            "where sc.id = :shopId")
    Integer getTotalRating(@Param("shopId") Long shopId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sc.ratingCount from ShopCount sc " +
            "where sc.id = :shopId")
    Integer getRatingCount(@Param("shopId") Long shopId);
}