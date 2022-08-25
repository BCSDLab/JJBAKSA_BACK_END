package com.jjbacsa.jjbacsabackend.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_count")
public class UserCount {

    @Id
    private Long id;

    @MapsId
    @Setter
    @OneToOne(optional = false)
    private UserEntity user;

    @Basic
    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Basic
    @Column(name = "scrap_count", nullable = false)
    private Integer scrapCount = 0;

    @Basic
    @Column(name = "friend_count", nullable = false)
    private Integer friendCount = 0;

    public void increaseReviewCount() {
        ++this.reviewCount;
    }

    public void decreaseReviewCount() {
        --this.reviewCount;
    }

    public void addScrapCount(int val) {
        scrapCount += val;
    }

    public void increaseScrapCount() {
        ++this.scrapCount;
    }

    public void decreaseScrapCount() {
        --this.scrapCount;
    }

    public void increaseFriendCount() {
        ++this.friendCount;
    }

    public void decreaseFriendCount() {
        --this.friendCount;
    }
}