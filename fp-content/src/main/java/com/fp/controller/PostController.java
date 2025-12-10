package com.fp.controller;

import com.fp.dto.content.PostRequestDTO;
import com.fp.dto.content.PostResponseDTO;
import com.fp.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "API for Content Post Management")
@RestController
@RequestMapping("/api/content/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    /**
     * Create a new post
     * @param postRequestDTO The post request form data
     * @return The created post details view object
     */
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestBody @Valid PostRequestDTO postRequestDTO
    ) {
        PostResponseDTO resp = postService.createPost(postRequestDTO);
        return ResponseEntity.ok(resp);
    }

    /**
     * Get post details by ID
     * @param postId The post ID
     * @return The post details view object
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long postId) {
        PostResponseDTO resp = postService.getPostById(postId);
        return ResponseEntity.ok(resp);
    }


    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequestDTO postRequestDTO
    ) {
        PostResponseDTO resp = postService.updatePost(postId, postRequestDTO);
        return ResponseEntity.ok(resp);
    }

}
