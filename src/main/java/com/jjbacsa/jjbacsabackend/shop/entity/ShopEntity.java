package com.jjbacsa.jjbacsabackend.shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@SQLDelete(sql = "UPDATE shop SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "shop")
public class ShopEntity extends BaseEntity {

    private static class ShopEntityBuilderImpl extends ShopEntityBuilder<ShopEntity, ShopEntityBuilderImpl> {

        @Override
        public ShopEntity build() {

            ShopEntity shop = new ShopEntity(this);
            shop.getShopCount().setShop(shop);

            return shop;
        }
    }

    @Basic
    @Column(name = "place_id", nullable = false)
    private String placeId;

    @Basic
    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Basic
    @Column(name = "x", nullable = false)
    private String x;

    @Basic
    @Column(name = "y", nullable = false)
    private String y;

    @Basic
    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Basic
    @Column(name="address",nullable = false)
    private String address;

    @Basic
    @Column(name="phone",nullable = true)
    private String phone;

    @Basic
    @Column(name="business_day",nullable = true)
    private String businessDay;

    @OneToOne(mappedBy = "shop", fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @Builder.Default
    private ShopCount shopCount = new ShopCount();
}