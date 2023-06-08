package com.jjbacsa.jjbacsabackend.review.service;

import java.util.List;

public interface InternalReviewService {

    //특정 사용자의 리뷰 Id 반환
    List<Long> getReviewIdsForUser(Long userId);

}
