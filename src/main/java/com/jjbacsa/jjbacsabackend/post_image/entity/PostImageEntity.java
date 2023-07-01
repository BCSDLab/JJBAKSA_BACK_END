package com.jjbacsa.jjbacsabackend.post_image.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@SQLDelete(sql = "UPDATE post_image SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "post_image")
public class PostImageEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @Setter
    private PostEntity post;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    @Setter
    private ImageEntity image;
}
