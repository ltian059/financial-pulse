package com.fp.dto.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO for creating or updating a content post.
 */

@Data
public class PostRequestDTO {
    @NotBlank(message = "Post Account ID cannot be blank")
    private String accountId;
    @NotBlank(message = "Post content cannot be blank")
    private String content; // rich text HTML
    private String imageLinks; // Comma-separated image URLs

    @Pattern(regexp = "ACTIVE|DRAFT", message = "Create a post status must be ACTIVE, or DRAFT")
    private String status; //
}
