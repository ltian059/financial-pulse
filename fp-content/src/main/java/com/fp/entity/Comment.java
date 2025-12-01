package com.fp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;

import java.time.Instant;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String content; // rich text HTML

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant modifiedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Column(nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long replyCount = 0L;

    private Long parentCommentId;
    
    public enum Status {
        ACTIVE,
        DELETED
    }
}

