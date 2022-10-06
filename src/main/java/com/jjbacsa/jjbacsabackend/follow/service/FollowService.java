package com.jjbacsa.jjbacsabackend.follow.service;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequestResponse;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowResponse;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {

    FollowRequestResponse request(FollowRequest request) throws Exception;

    FollowResponse accept(Long requestId) throws Exception;

    void reject(Long requestId) throws Exception;

    void cancel(Long requestId) throws Exception;

    void delete(FollowRequest request) throws Exception;

    Page<FollowRequestResponse> getSendRequests(Integer page, Integer pageSize) throws Exception;

    Page<FollowRequestResponse> getReceiveRequests(Integer page, Integer pageSize) throws Exception;

    Page<UserResponse> getFollowers(String cursor, Integer pageSize) throws Exception;
}
