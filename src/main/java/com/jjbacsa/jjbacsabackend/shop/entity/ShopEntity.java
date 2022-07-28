package com.jjbacsa.jjbacsabackend.shop.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.entity.UserCount;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @OneToOne(mappedBy = "shop", optional = false, cascade = CascadeType.PERSIST)
    @Builder.Default
    private ShopCount shopCount = new ShopCount();

    public void update(ShopRequest shopRequest) {

    }

    public void setTotalRating(Integer totalRating) {
        shopCount.setTotalRating(totalRating);
    }

    public void increaseRatingCount() {
        shopCount.increaseRatingCount();
    }

    public void decreaseRatingCount() {
        shopCount.decreaseRatingCount();
    }
}