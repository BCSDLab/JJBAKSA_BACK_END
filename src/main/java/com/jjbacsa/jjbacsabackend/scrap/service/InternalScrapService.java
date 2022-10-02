package com.jjbacsa.jjbacsabackend.scrap.service;

import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;

public interface InternalScrapService {

    ScrapDirectoryEntity getScrapDirectoryById(Long scrapDirectoryId);

    void addScrapCount(Long scrapDirectoryId, int delta);
}
