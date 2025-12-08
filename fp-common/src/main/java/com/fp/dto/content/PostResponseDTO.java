package com.fp.dto.content;

import lombok.Data;

import java.time.Instant;

@Data
public class PostResponseDTO {
    private Long id;
    private String accountId;
    private String content; // rich text HTML
    private String imageLinks; // Comma-separated image URLs
    private Long likeCount;
    private Long viewCount;
    private Long commentCount;
    private Long repostCount;
    private Long quoteCount;
    private Instant createdAt;
    private Instant modifiedAt;
    private String status; // Post status as String (ACTIVE; DRAFT; HIDDEN)
}
