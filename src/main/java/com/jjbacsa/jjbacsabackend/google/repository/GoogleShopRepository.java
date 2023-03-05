package com.jjbacsa.jjbacsabackend.google.repository;

import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoogleShopRepository extends JpaRepository<GoogleShopEntity, Long> {
    GoogleShopEntity findByPlaceId(String placeId);
}
