package com.jjbacsa.jjbacsabackend.follow.service;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService{

    private final FollowRepository followRepository;

    @Override
    public void request(FollowRequest request) throws Exception {

    }

    @Override
    public void accept(Long requestId) throws Exception {

    }

    @Override
    public void reject(Long requestId) throws Exception {

    }

    @Override
    public void cancel(Long requestId) throws Exception {

    }

    @Override
    public void delete(FollowRequest request) throws Exception {

    }
}
