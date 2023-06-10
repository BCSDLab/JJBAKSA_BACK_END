package com.jjbacsa.jjbacsabackend.user.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@SQLDelete(sql = "UPDATE user SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "user")
public class UserEntity extends BaseEntity {

    private static class UserEntityBuilderImpl extends UserEntityBuilder<UserEntity, UserEntityBuilderImpl> {

        @Override
        public UserEntity build() {

            id(null);
            UserEntity user = new UserEntity(this);
            user.getUserCount().setUser(user);

            return user;
        }
    }

    @Basic
    @Column(name = "account")
    private String account;

    @Basic
    @Column(name = "password")
    @Setter
    private String password;

    @Basic
    @Column(name = "email", nullable = false)
    @Setter
    private String email;

    @Basic
    @Column(name = "nickname", nullable = false)
    @Setter
    private String nickname;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_logged_at", nullable = false)
    @CreatedDate
    private Date lastLoggedAt;

    @Basic
    @Column(name = "auth_email", nullable = false, columnDefinition = "boolean default false")
    @Setter
    private boolean authEmail = false;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "profile_image_id")
    @Setter
    private ImageEntity profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @Builder.Default
    private UserCount userCount = new UserCount();

    public void update(UserRequest userRequest) {

    }

    public void modifyUserRole(UserType userType){
        this.userType = userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAccount(), that.getAccount()) && Objects.equals(getPassword(), that.getPassword()) && Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getNickname(), that.getNickname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAccount(), getPassword(), getEmail(), getNickname());
    }
}
