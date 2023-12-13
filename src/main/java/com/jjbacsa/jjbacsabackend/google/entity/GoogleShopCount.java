package com.jjbacsa.jjbacsabackend.google.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "google_shop_count")
public class GoogleShopCount {
    @Id
    private Long id;

    @Setter
    @MapsId
    @OneToOne(optional = false)
    private GoogleShopEntity shop;

    @Basic
    @Setter
    @Column(name = "total_rating", nullable = false)
    private Integer totalRating = 0;

    @Basic
    @Setter
    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;
}
