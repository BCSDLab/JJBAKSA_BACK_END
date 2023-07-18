package com.jjbacsa.jjbacsabackend.post.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post_image.entity.PostImageEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@SQLDelete(sql = "UPDATE post SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "post")
public class PostEntity extends BaseEntity {

    private static class PostEntityBuilderImpl extends PostEntity.PostEntityBuilder<PostEntity, PostEntity.PostEntityBuilderImpl> {

        @Override
        public PostEntity build() {
            PostEntity postEntity = new PostEntity(this);
            if (postEntity.getPostImages() != null) {
                for (PostImageEntity image : postEntity.getPostImages()) {
                    image.setPost(postEntity);
                }
            }
            return postEntity;
        }

    }

    @Basic
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostImageEntity> postImages = new ArrayList<>();

    // 연관관계 메서드
    public void addPostImageEntity(PostImageEntity postImage) {
        if (postImage != null) {
            this.postImages.add(postImage);
            postImage.setPost(this);
        }
    }

    public void update(PostRequest postRequest) {
        if (postRequest.getTitle() != null) {
            this.title = postRequest.getTitle();
        }
        if (postRequest.getContent() != null) {
            this.content = postRequest.getContent();
        }
    }
}