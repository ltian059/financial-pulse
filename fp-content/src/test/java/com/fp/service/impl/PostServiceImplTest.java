package com.fp.service.impl;

import com.fp.dto.content.PostRequestDTO;
import com.fp.dto.content.PostResponseDTO;
import com.fp.entity.Post;
import com.fp.repository.PostRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postServiceImpl;

    @Test
    public void createPost_withActiveStatus_shouldSaveAndReturnResponse(){
        // 1. Prepare the PostRequestDTO with active status
        PostRequestDTO request = new PostRequestDTO();
        request.setStatus("ACTIVE");
        request.setContent("test content <b>bold</b>");
        request.setAccountId("acc-123");
        request.setImageLinks("http://example.com/image1.jpg,http://example.com/image2.jpg");

        // 2. Stubbing: Define repo behavior: when saving, return the entity with an ID‘
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0, Post.class);
            post.setId(1L);
            return post;
        });

        // 3. Call the service method
        PostResponseDTO response = postServiceImpl.createPost(request);

        // 4. Verify the repository save method was called once
        ArgumentCaptor<Post> argumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository, times(1)).save(argumentCaptor.capture());

        Post savedPost = argumentCaptor.getValue();

        // 5. Assert the saved entity fields
        assertEquals(Post.Status.ACTIVE, savedPost.getStatus());
        assertEquals("test content <b>bold</b>", savedPost.getContent());
        assertEquals("acc-123", savedPost.getAccountId());
        assertEquals("http://example.com/image1.jpg,http://example.com/image2.jpg", savedPost.getImageLinks());

        // 6. Assert the response DTO fields
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("test content <b>bold</b>", response.getContent());
        assertEquals("acc-123", response.getAccountId());
        assertEquals("http://example.com/image1.jpg,http://example.com/image2.jpg",
                response.getImageLinks());
        assertNotNull(response.getCreatedAt());
        assertEquals(0L, response.getLikeCount());

    }

    @Test
    public void createPost_withDraftStatus_shouldSaveAndReturnResponse(){
        // 1. Stubbing
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post p = invocation.getArgument(0, Post.class);
            p.setId(2L);
            return p;
        });

        // 2. Prepare post request DTO
        PostRequestDTO req = new PostRequestDTO();
        req.setStatus("DRAFT");
        req.setContent("test content <b>bold</b>");
        req.setAccountId("acc-456");
        req.setImageLinks("http://example.com/image3.jpg");

        // 3. Call the service method
        PostResponseDTO post = postServiceImpl.createPost(req);
        // 4. Verify repository save method called once
        ArgumentCaptor<Post> argumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository, times(1)).save(argumentCaptor.capture());

        Post entityToSave = argumentCaptor.getValue();
        // 5. Assert the entity to save fields
        assertEquals(Post.Status.DRAFT, entityToSave.getStatus());
        assertEquals("test content <b>bold</b>", entityToSave.getContent());
        assertEquals("acc-456", entityToSave.getAccountId());
        assertEquals("http://example.com/image3.jpg", entityToSave.getImageLinks());

        // 6. Assert the response DTO fields
        assertNotNull(post);
        assertEquals(2L, post.getId());
        assertEquals("DRAFT", post.getStatus());
        assertEquals("test content <b>bold</b>", post.getContent());
        assertEquals("acc-456", post.getAccountId());
        assertEquals("http://example.com/image3.jpg", post.getImageLinks());
        assertNotNull(post.getCreatedAt());
        assertEquals(0L, post.getLikeCount());

    }

    @Test
    public void createPost_withInvalidStatus_shouldThrowException(){
        // 1. Prepare PostRequestDTO with invalid status
        PostRequestDTO request = new PostRequestDTO();
        request.setStatus("INVALID_STATUS");
        request.setContent("Some content");
        request.setAccountId("acc-789");
        request.setImageLinks("http://example.com/image4.jpg");

        // 2. Call the service method and expect an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            postServiceImpl.createPost(request);
        });

        // 3. Verify repository save method was never called
        verify(postRepository, never()).save(any(Post.class));
    }

}
