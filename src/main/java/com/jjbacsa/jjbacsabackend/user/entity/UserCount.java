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
    @Setter
    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Basic
    @Setter
    @Column(name = "scrap_count", nullable = false)
    private Integer scrapCount = 0;

    @Basic
    @Setter
    @Column(name = "friend_count", nullable = false)
    private Integer friendCount = 0;
}