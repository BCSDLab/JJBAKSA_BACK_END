package com.jjbacsa.jjbacsabackend.shop.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "shop_count")
public class ShopCount {

    @Id
    private Long id;

    @MapsId
    @Setter
    @OneToOne(optional = false)
    private ShopEntity shop;

    @Setter
    @Basic
    @Column(name = "total_rating", nullable = false)
    private Integer totalRating = 0;

    @Basic
    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    public void increaseRatingCount() {
        ++this.ratingCount;
    }

    public void decreaseRatingCount() {
        --this.ratingCount;
    }
}