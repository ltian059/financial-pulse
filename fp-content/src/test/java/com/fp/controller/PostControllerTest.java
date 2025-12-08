package com.fp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.dto.content.PostRequestDTO;
import com.fp.dto.content.PostResponseDTO;
import com.fp.entity.Post;
import com.fp.service.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createPost_withValidRequest_shouldReturn200AndResponseBody() throws Exception {
        // 1. Prepare the request body dto
        PostRequestDTO req = new PostRequestDTO();
        req.setContent("Sample post content");
        req.setAccountId("acc-123");
        req.setStatus("DRAFT");
        req.setImageLinks("img1.jpg,img2.jpg");

        String jsonBody = objectMapper.writeValueAsString(req);

        // 2. stub service response dto
        PostResponseDTO resp = new PostResponseDTO();
        Post post = new Post();
        BeanUtils.copyProperties(post, resp);
        resp.setId(1L);
        resp.setContent("Sample post content");
        resp.setAccountId("acc-123");
        resp.setStatus("DRAFT");
        resp.setImageLinks("img1.jpg,img2.jpg");

        when(postService.createPost(any(PostRequestDTO.class))).thenReturn(resp);

        // 3. Use mockMvc to perform the POST request and assert the response
        mockMvc.perform(post("/api/content/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Sample post content"))
                .andExpect(jsonPath("$.accountId").value("acc-123"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.imageLinks").value("img1.jpg,img2.jpg"))
                .andExpect(jsonPath("$.likeCount").value(0L))
                .andExpect(jsonPath("$.createdAt").exists());

        // 4. Verify that the service method was called 1 time
        verify(postService, times(1)).createPost(any(PostRequestDTO.class));
    }

    @Test
    public void createPost_withInvalidRequest_shouldThrowException() throws Exception {
        // 1. Prepare the invalid request body dto
        PostRequestDTO req = new PostRequestDTO();
        req.setContent(""); // Invalid: empty content
        req.setAccountId("");
        req.setStatus("INVALID_STATUS"); // Invalid status

        String jsonBody = objectMapper.writeValueAsString(req);

        // 2. Use mockMvc to perform the POST request and assert the response
        mockMvc.perform(post("/api/content/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Post content cannot be blank")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Post Account ID cannot be blank")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Create a post status must be ACTIVE, or DRAFT")))
        ;
    }
}
