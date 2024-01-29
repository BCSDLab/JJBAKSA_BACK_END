package com.jjbacsa.jjbacsabackend.scrap.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryRequest;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import lombok.Builder;
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
@SQLDelete(sql = "UPDATE scrap_directory SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "scrap_directory")
public class ScrapDirectoryEntity extends BaseEntity {

        private static class ScrapDirectoryEntityBuilderImpl extends ScrapDirectoryEntityBuilder<ScrapDirectoryEntity, ScrapDirectoryEntityBuilderImpl> {

        @Override
        public ScrapDirectoryEntity build() {

            id(null);
            ScrapDirectoryEntity directory = new ScrapDirectoryEntity(this);
            directory.getScrapDirectoryCount().setDirectory(directory);

            return directory;
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Basic
    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(mappedBy = "directory", fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @Builder.Default
    private ScrapDirectoryCount scrapDirectoryCount = new ScrapDirectoryCount();

    public void update(ScrapDirectoryRequest request) {
        name = request.getName();
    }
}