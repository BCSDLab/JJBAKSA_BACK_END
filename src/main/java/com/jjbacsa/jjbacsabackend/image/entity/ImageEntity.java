package com.jjbacsa.jjbacsabackend.image.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
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
@SQLDelete(sql = "UPDATE image SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "image")
public class ImageEntity extends BaseEntity {

    @Basic
    @Column(name = "path", nullable = false)
    private String path;

    @Basic(optional = false)
    @Column(name = "original_name")
    private String originalName;

    public void updateImage(String path, String originalName){
        this.path = path;
        this.originalName = originalName;
    }
}