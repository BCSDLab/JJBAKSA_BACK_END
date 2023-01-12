package com.jjbacsa.jjbacsabackend.post.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.etc.enums.BoardType;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
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
@SQLDelete(sql = "UPDATE post SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "post")
public class PostEntity extends BaseEntity {

    @Basic
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_type", nullable = false)
    private BoardType boardType;

    public void update(PostRequest postRequest){
        if(postRequest.getTitle() != null){
            this.title = postRequest.getTitle();
        }
        if(postRequest.getContent() != null){
            this.content = postRequest.getContent();
        }
    }
}