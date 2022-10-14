package com.jjbacsa.jjbacsabackend.shop.repository.querydsl;

import com.jjbacsa.jjbacsabackend.shop.dto.response.QShopSummaryResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static com.jjbacsa.jjbacsabackend.shop.entity.QShopEntity.shopEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DslShopRepositoryImpl implements DslShopRepository{

    private final JPAQueryFactory queryFactory;

    //TODO: keyword 전처리 없이도 수행되는지 확인
    @Override
    public List<ShopSummaryResponse> search(String keyword, String category) {

        BooleanBuilder builder=new BooleanBuilder();

        NumberTemplate booleanTemplate= Expressions.numberTemplate(Double.class,
                "function('match',{0},{1},{2})",shopEntity.placeName,shopEntity.address,keyword);

        builder.and(booleanTemplate.gt(0));

        if(category!=null) {
            builder.and(shopEntity.categoryName.eq(category));
        }


        return queryFactory.select(new QShopSummaryResponse(shopEntity.id.as("shopId"),shopEntity.placeId.as("placeId"),shopEntity.placeName.as("placeName"),shopEntity.address.as("address"),shopEntity.x.as("x"),shopEntity.y.as("y"),
                Expressions.numberTemplate(Double.class,
                        "function('match',{0},{1},{2})",shopEntity.placeName,shopEntity.address,keyword).as("score")))
                .from(shopEntity)
                .where(builder)
                .fetch();
    }

    @Override
    public List<ShopSummaryResponse> findAllByCategoryName(String categoryName) {
        return queryFactory.select(new QShopSummaryResponse(shopEntity.id, shopEntity.placeId, shopEntity.placeName, shopEntity.address, shopEntity.x, shopEntity.y))
                .from(shopEntity)
                .where(shopEntity.categoryName.eq(categoryName))
                .fetch();
    }

    @Override
    public List<ShopSummaryResponse> findByPlaceNameContaining(String keyword) {
        return queryFactory.select(new QShopSummaryResponse(shopEntity.id, shopEntity.placeId, shopEntity.placeName, shopEntity.address, shopEntity.x, shopEntity.y))
                .from(shopEntity)
                .where(shopEntity.placeName.contains(keyword))
                .fetch();
    }
}
