package com.jjbacsa.jjbacsabackend.scrap.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "scrap_directory_count")
public class ScrapDirectoryCount {

    @Id
    private Long id;

    @MapsId
    @Setter
    @OneToOne(optional = false)
    private ScrapDirectoryEntity directory;

    @Basic
    @Column(name = "scrap_count", nullable = false)
    private Integer scrapCount = 0;

    public void addScrapCount(int val) {
        scrapCount += val;
    }

    public void increaseScrapCount() {
        ++this.scrapCount;
    }

    public void decreaseScrapCount() {
        --this.scrapCount;
    }

}