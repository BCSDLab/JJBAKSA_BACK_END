package com.jjbacsa.jjbacsabackend.shop.repository;

import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long> {

    Optional<ShopEntity> findByPlaceId(String placeId);

    boolean existsByPlaceId(String placeId);

    Optional<ShopEntity> findByPlaceNameContaining(String placeName);
}