package com.jjbacsa.jjbacsabackend.shop.repository;

import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long> {

    Optional<ShopEntity> findByPlaceId(String placeId);

    boolean existsByPlaceId(String placeId);

    //full text search (boolean mode)
    @Query(value="SELECT s.place_id as placeId,s.place_name as placeName,s.address as address," +
            "s.x as x ,s.y as y ,MATCH(s.place_name,s.address) AGAINST(:keyword in boolean mode) as score "+
            "FROM shop s "+
            "WHERE MATCH(s.place_name,s.address) AGAINST(:keyword in boolean mode) ", nativeQuery = true
    )
    List<Tuple> search(@Param("keyword") String keyword);

    @Query(value="SELECT s.place_id as placeId,s.place_name as placeName,s.address as address," +
            "s.x as x ,s.y as y ,MATCH(s.place_name,s.address) AGAINST(:keyword in boolean mode) as score "+
            "FROM shop s "+
            "WHERE MATCH(s.place_name,s.address) AGAINST(:keyword in boolean mode) and s.category_name=:category ", nativeQuery = true
    )
    List<Tuple> searchWithCategory(@Param("keyword")String keyword, @Param("category")String category);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sc.totalRating from ShopCount sc " +
            "where sc.id = :shopId")
    Integer getTotalRating(@Param("shopId") Long shopId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sc.ratingCount from ShopCount sc " +
            "where sc.id = :shopId")
    Integer getRatingCount(@Param("shopId") Long shopId);
}