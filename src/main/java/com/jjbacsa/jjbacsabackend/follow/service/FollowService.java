package com.jjbacsa.jjbacsabackend.follow.service;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequestResponse;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import org.springframework.data.domain.Page;

public interface FollowService {

    void request(FollowRequest request) throws Exception;

    void accept(Long requestId) throws Exception;

    void reject(Long requestId) throws Exception;

    void cancel(Long requestId) throws Exception;

    void delete(FollowRequest request) throws Exception;

    Page<FollowRequestResponse> getSendRequests(int page) throws Exception;

    Page<FollowRequestResponse> getReceiveRequests(int page) throws Exception;

    Page<UserResponse> getFollowers(String cursor) throws Exception;
}
