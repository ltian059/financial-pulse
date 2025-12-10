package com.fp.service;

import com.fp.dto.common.PageResponseDTO;
import com.fp.dto.content.ListPostsRequestDTO;
import com.fp.dto.content.PostRequestDTO;
import com.fp.dto.content.PostResponseDTO;

public interface PostService {
    PostResponseDTO createPost(PostRequestDTO postRequestDTO);

    PostResponseDTO getPostById(Long postId);

    PageResponseDTO<PostResponseDTO> listPosts(ListPostsRequestDTO listPostsRequestDTO);

    void deletePost(Long postId);

    PostResponseDTO updatePost(Long postId, PostRequestDTO postRequestDTO);

    PageResponseDTO<PostResponseDTO> getAccountPosts(String accountId);
}
