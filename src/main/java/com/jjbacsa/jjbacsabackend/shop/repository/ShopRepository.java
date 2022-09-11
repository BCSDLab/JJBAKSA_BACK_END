package com.jjbacsa.jjbacsabackend.shop.repository;

import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long>, ShopCustomRepository {

    Optional<ShopEntity> findByPlaceId(String placeId);

    boolean existsByPlaceId(String placeId);

    ShopEntity save(ShopEntity shopEntity);

    //full text search (natural language mode)
    @Query(value="SELECT s.place_id as placeId,s.place_name as placeName,s.address as address," +
            "s.x as x ,s.y as y ,MATCH(s.place_name,s.address) AGAINST(:keyword) as score "+
            "FROM shop s "+
            "WHERE MATCH(s.place_name,s.address) AGAINST(:keyword) ", nativeQuery = true
    )
    List<Tuple> search(@Param("keyword") String keyword);

    @Query(value="SELECT s.place_id as placeId,s.place_name as placeName,s.address as address," +
            "s.x as x ,s.y as y ,MATCH(s.place_name,s.address) AGAINST(:keyword) as score "+
            "FROM shop s "+
            "WHERE MATCH(s.place_name,s.address) AGAINST(:keyword) and s.category_name=:category ", nativeQuery = true
    )
    List<Tuple> searchWithCategory(@Param("keyword")String keyword, @Param("category")String category);

}