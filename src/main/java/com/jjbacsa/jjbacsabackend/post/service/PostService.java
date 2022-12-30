package com.jjbacsa.jjbacsabackend.post.service;

import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;

public interface PostService {
    PostResponse getPost(Long postId);
    PostResponse createPost(PostRequest postRequest);
    PostResponse modifyAdminPost(PostRequest postRequest, Long postId);
    void deletePost(Long postId);
    Page<PostResponse> getPosts(String boardType, Integer page, Integer size);

}
