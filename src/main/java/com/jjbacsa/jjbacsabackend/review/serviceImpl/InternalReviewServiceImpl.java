package com.jjbacsa.jjbacsabackend.review.serviceImpl;

import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.repository.ReviewRepository;
import com.jjbacsa.jjbacsabackend.review.service.InternalReviewService;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class InternalReviewServiceImpl implements InternalReviewService {

    private final ReviewRepository reviewRepository;

    //todo: 구글 상점으로 변경되면 추후 수정
    @Override
    public List<Long> getReviewIdsForUser(Long userId) {

        return reviewRepository.findAllByWriter(userId)
                .stream()
                .map(ReviewEntity::getShop)
                .map(ShopEntity::getId)
                .collect(Collectors.toList());
    }
}
