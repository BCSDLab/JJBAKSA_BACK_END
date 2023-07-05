package com.jjbacsa.jjbacsabackend.review.service;

<<<<<<< HEAD
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
=======
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

>>>>>>> develop
import java.util.List;

public interface InternalReviewService {

    //특정 사용자의 리뷰 Id 반환
    List<Long> getReviewIdsForUser(UserEntity user);

    List<ReviewEntity> findReviewsByWriter(UserEntity user);

    void deleteReview(ReviewEntity review) throws Exception;

}
