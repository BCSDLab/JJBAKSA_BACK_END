package com.jjbacsa.jjbacsabackend.search.service;

import com.jjbacsa.jjbacsabackend.search.dto.AutoCompleteResponse;
import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;

public interface SearchService {

    AutoCompleteResponse getAutoCompletes(String word);
    TrendingResponse getTrending();
}