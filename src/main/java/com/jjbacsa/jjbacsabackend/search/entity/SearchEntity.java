package com.jjbacsa.jjbacsabackend.search.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Getter
@NoArgsConstructor
@Entity
@DynamicInsert
@Table(name="search")
public class SearchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Basic
    @Column(name="content",nullable = false)
    private String content;

    @Column(name="score",nullable=false)
    private Long score;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    @CreatedDate
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false, updatable = false, insertable = false)
    @LastModifiedDate
    private Date updatedAt;

    public SearchEntity(String content) {
        this.content = content;
    }

    public Long updateScore(Long score){
        this.score=score;
        return this.score;
    }
}
