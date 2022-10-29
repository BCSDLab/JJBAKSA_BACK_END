package com.jjbacsa.jjbacsabackend.user.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@SQLDelete(sql = "UPDATE auth_email SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "auth_email")
public class AuthEmailEntity extends BaseEntity {

    @Basic
    @Column(name = "secret", nullable = false)
    private String secret;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expired_at")
    private Date expiredAt;

    @Setter
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
