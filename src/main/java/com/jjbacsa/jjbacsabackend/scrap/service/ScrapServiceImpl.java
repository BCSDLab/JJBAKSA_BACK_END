package com.jjbacsa.jjbacsabackend.scrap.service;

import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequest;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowRequestResponse;
import com.jjbacsa.jjbacsabackend.follow.dto.FollowResponse;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.follow.mapper.FollowMapper;
import com.jjbacsa.jjbacsabackend.follow.mapper.FollowRequestMapper;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRepository;
import com.jjbacsa.jjbacsabackend.follow.repository.FollowRequestRepository;
import com.jjbacsa.jjbacsabackend.follow.service.FollowService;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryResponse;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapResponse;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScrapServiceImpl implements ScrapService {

    @Override
    public ScrapDirectoryResponse createDirectory(ScrapDirectoryRequest request) throws Exception {
        return null;
    }

    @Override
    public Page<ScrapDirectoryResponse> getDirectories(String cursor, Integer pageSize) throws Exception {
        return null;
    }

    @Override
    public ScrapDirectoryResponse updateDirectory(Long directoryId, ScrapDirectoryRequest request) throws Exception {
        return null;
    }

    @Override
    public void deleteDirectory(Long directoryId) throws Exception {

    }

    @Override
    public ScrapResponse create(ScrapRequest request) throws Exception {
        return null;
    }

    @Override
    public Page<ScrapResponse> getScraps(Long directoryId, Long cursor, Integer pageSize) throws Exception {
        return null;
    }

    @Override
    public ScrapResponse move(Long scrapId, ScrapRequest request) throws Exception {
        return null;
    }

    @Override
    public void delete(Long scrapId) throws Exception {

    }
}
