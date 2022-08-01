package com.jjbacsa.jjbacsabackend.scrap.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@SQLDelete(sql = "UPDATE scrap_directory SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "scrap_directory")
public class ScrapDirectoryEntity extends BaseEntity {

    @Basic
    @Column(name = "name", nullable = false)
    private String name;

    public void update(String name) {
        this.name = name;
    }
}