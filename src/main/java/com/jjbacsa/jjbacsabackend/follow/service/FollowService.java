package com.jjbacsa.jjbacsabackend.follow.service;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;

public interface FollowService {

    void request(FollowRequest request) throws Exception;
    void accept(Long requestId) throws Exception;
    void reject(Long requestId) throws Exception;
    void delete(FollowRequest request) throws  Exception;
}
