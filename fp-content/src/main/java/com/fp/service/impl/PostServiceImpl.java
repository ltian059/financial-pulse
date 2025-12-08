package com.fp.service.impl;

import com.fp.dto.common.PageResponseDTO;
import com.fp.dto.content.ListPostsRequestDTO;
import com.fp.dto.content.PostRequestDTO;
import com.fp.dto.content.PostResponseDTO;
import com.fp.entity.Post;
import com.fp.repository.PostRepository;
import com.fp.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Override
    public PostResponseDTO createPost(PostRequestDTO postRequestDTO) {
        // 1. Prepare the post entity from the request DTO
        Post post =  fromRequestDTO(postRequestDTO);
        // 2. Save the post entity using the repository.
        Post save = postRepository.save(post);
        //3. Prepare and return the response DTO.
        return buildResponseDTO(save);
    }

    private PostResponseDTO buildResponseDTO(Post save) {
        PostResponseDTO postResponseDTO = new PostResponseDTO();
        BeanUtils.copyProperties(save, postResponseDTO);
        // Convert the Status enum to String
        postResponseDTO.setStatus(save.getStatus().name());
        return postResponseDTO;
    }

    private Post fromRequestDTO(PostRequestDTO postRequestDTO) {
        Post post = new Post();
        BeanUtils.copyProperties(postRequestDTO, post);
        // Prepare the Status enum.
        post.setStatus(Post.Status.valueOf(postRequestDTO.getStatus()));
        return post;
    }

    @Override
    public PostResponseDTO getPostById(Long postId) {
        return null;
    }

    @Override
    public PageResponseDTO<PostResponseDTO> listPosts(ListPostsRequestDTO listPostsRequestDTO) {
        return null;
    }

    @Override
    public void deletePost(Long postId) {

    }

    @Override
    public PostResponseDTO updatePost(Long postId, PostRequestDTO postRequestDTO) {
        return null;
    }
}
