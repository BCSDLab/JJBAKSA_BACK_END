package com.jjbacsa.jjbacsabackend.user.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
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
    private String password;

    @Basic
    @Column(name = "email", nullable = false)
    private String email;

    @Basic
    @Column(name = "nickname", nullable = false)
    private String nickname;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "profile_image_id")
    private ImageEntity profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @Builder.Default
    private UserCount userCount = new UserCount();

    public void update(UserRequest userRequest) {

    }

    public void increaseReviewCount() {
        userCount.increaseReviewCount();
    }

    public void decreaseReviewCount() {
        userCount.decreaseReviewCount();
    }

    public void increaseFriendCount() {
        userCount.increaseFriendCount();
    }

    public void decreaseFriendCount() {
        userCount.decreaseFriendCount();
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
