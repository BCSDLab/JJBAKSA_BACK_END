package com.jjbacsa.jjbacsabackend.review.serviceImpl;

import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.review.service.InternalReviewService;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class InternalReviewServiceImpl implements InternalReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public List<Long> getReviewIdsForUser(UserEntity user) {

        return reviewRepository.findAllByWriter(user)
                .stream()
                .map(ReviewEntity::getShop)
                .map(GoogleShopEntity::getId)
                .collect(Collectors.toList());
    }
}
