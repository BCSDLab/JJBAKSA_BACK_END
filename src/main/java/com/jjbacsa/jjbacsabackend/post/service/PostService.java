package com.jjbacsa.jjbacsabackend.post.service;

import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface PostService {
    PostResponse getPost(Long postId);
    PostResponse createPost(PostRequest postRequest);
    PostResponse modifyUserPost(PostRequest postRequest, Long postId);
    PostResponse modifyAdminPost(PostRequest postRequest, Long postId);
    void deletePost(Long postId);
    Page<PostResponse> getFAQs(Integer page, Integer size);
    Page<PostResponse> getNotices(Integer page, Integer size);


}
