package com.jjbacsa.jjbacsabackend.scrap.service;

import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;

public interface InternalScrapService {

    ScrapEntity getScrapById(Long scrapId);

    ScrapDirectoryEntity getScrapDirectoryById(Long scrapDirectoryId);

    void addScrapCount(Long scrapDirectoryId, int delta);
}
