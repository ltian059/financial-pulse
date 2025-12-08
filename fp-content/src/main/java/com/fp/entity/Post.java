package com.fp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String accountId;
    
    @Column(nullable = false, columnDefinition = "text")
    private String content; // rich text HTML

    @Column(nullable = false)
    @Default
    private Instant createdAt = Instant.now();

    private Instant modifiedAt;

    private String imageLinks;

    @Column(nullable = false)
    @Default
    private Long likeCount = 0L;

    @Column(nullable = false)
    @Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Default
    private Long commentCount = 0L;

    @Column(nullable = false)
    @Default
    private Long repostCount = 0L;

    @Column(nullable = false)
    @Default
    private Long quoteCount = 0L;

    private Long repostOfPostId;

    private Long quoteOfPostId;

    @Column(columnDefinition = "text")
    private String labels;

    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Default
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE,
        DELETED,
        HIDDEN,
        DRAFT
    }
}
