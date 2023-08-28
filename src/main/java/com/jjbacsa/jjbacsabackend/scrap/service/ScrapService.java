package com.jjbacsa.jjbacsabackend.scrap.service;

import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopScrapResponse;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryResponse;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapResponse;
import org.springframework.data.domain.Page;

public interface ScrapService {

    ScrapDirectoryResponse createDirectory(ScrapDirectoryRequest request) throws Exception;

    Page<ScrapDirectoryResponse> getDirectories(String cursor, Integer pageSize) throws Exception;

    ScrapDirectoryResponse updateDirectory(Long directoryId, ScrapDirectoryRequest request) throws Exception;

    void deleteDirectory(Long directoryId) throws Exception;

    ScrapResponse create(ScrapRequest request) throws Exception;

    Page<ShopScrapResponse> getScraps(Long directoryId, Long cursor, Integer pageSize) throws Exception;

    ScrapResponse move(Long scrapId, ScrapRequest request) throws Exception;

    void delete(Long scrapId) throws Exception;

    Page<ShopScrapResponse> getScrapShops(Long userId, Long cursor, Integer pageSize) throws Exception;

    ShopScrapResponse getScrapShop(Long scrapId) throws Exception;
}
