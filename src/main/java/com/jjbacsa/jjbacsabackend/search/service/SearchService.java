package com.jjbacsa.jjbacsabackend.search.service;

import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;

public interface SearchService {
    TrendingResponse getTrending();

    void saveTrending(String keyword);
}
