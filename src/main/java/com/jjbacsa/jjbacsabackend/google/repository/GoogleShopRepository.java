package com.jjbacsa.jjbacsabackend.google.repository;

import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface GoogleShopRepository extends JpaRepository<GoogleShopEntity, Long> {
    Optional<GoogleShopEntity> findByPlaceId(String placeId);

    GoogleShopEntity getByPlaceId(String placeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sc.totalRating from GoogleShopCount sc " +
            "where sc.id = :shopId")
    Integer getTotalRating(@Param("shopId") Long shopId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sc.ratingCount from GoogleShopCount sc " +
            "where sc.id = :shopId")
    Integer getRatingCount(@Param("shopId") Long shopId);

    boolean existsByPlaceId(String placeId);
}
