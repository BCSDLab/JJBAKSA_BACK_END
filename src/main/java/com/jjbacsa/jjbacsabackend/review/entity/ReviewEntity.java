package com.jjbacsa.jjbacsabackend.review.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewRequest;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@SQLDelete(sql = "UPDATE review SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "review")
public class ReviewEntity extends BaseEntity {

    private static class ReviewEntityBuilderImpl extends ReviewEntityBuilder<ReviewEntity, ReviewEntityBuilderImpl> {

        @Override
        public ReviewEntity build() {
            ReviewEntity reviewEntity = new ReviewEntity(this);
            if (reviewEntity.getReviewImages() != null) {
                for (ReviewImageEntity image : reviewEntity.getReviewImages()) {
                    image.setReview(reviewEntity);
                }
            }
            return reviewEntity;
        }

    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private UserEntity writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "google_place_id", referencedColumnName = "place_id")
    private GoogleShopEntity shop;

    @Setter
    @Basic(optional = false)
    @Lob
    @Column(name = "content")
    private String content;

    @Setter
    @Column(name = "rate")
    private Integer rate;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewImageEntity> reviewImages = new ArrayList<>();

    // 연관관계 메서드
    public void addReviewImageEntity(ReviewImageEntity reviewImageEntity) {
        reviewImages.add(reviewImageEntity);
        reviewImageEntity.setReview(this);
    }

    public void update(ReviewRequest reviewRequest) {
        this.content = reviewRequest.getContent();
        this.rate = reviewRequest.getRate();
    }
}