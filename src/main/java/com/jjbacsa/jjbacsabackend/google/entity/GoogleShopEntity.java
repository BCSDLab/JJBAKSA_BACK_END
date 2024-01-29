package com.jjbacsa.jjbacsabackend.google.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 리뷰 쓸 때 상점 저장
 * -> place_id 저장
 */

@Getter
@Entity
@SQLDelete(sql = "UPDATE google_shop SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "google_shop")
public class GoogleShopEntity extends BaseEntity implements Serializable {

    private static class GoogleShopEntityBuilderImpl extends GoogleShopEntityBuilder<GoogleShopEntity, GoogleShopEntityBuilderImpl> {
        @Override
        public GoogleShopEntity build() {
            GoogleShopEntity shop = new GoogleShopEntity(this);
            shop.getShopCount().setShop(shop);

            return shop;
        }
    }

    @Basic
    @Column(name = "place_id", nullable = false)
    private String placeId;

    @OneToOne(mappedBy = "shop", fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @Builder.Default
    private GoogleShopCount shopCount = new GoogleShopCount();
}
