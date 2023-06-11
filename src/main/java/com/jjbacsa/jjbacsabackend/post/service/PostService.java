package com.jjbacsa.jjbacsabackend.post.service;

import com.jjbacsa.jjbacsabackend.post.dto.request.PostCursorRequest;
import com.jjbacsa.jjbacsabackend.post.dto.request.PostRequest;
import com.jjbacsa.jjbacsabackend.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;

import java.io.IOException;


public interface PostService {

    PostResponse getPost(Long postId);

    PostResponse createPost(PostRequest postRequest) throws IOException;

    PostResponse modifyAdminPost(PostRequest postRequest, Long postId) throws IOException;

    void deletePost(Long postId);

    Page<PostResponse> getPosts(PostCursorRequest postPageRequest);

}
