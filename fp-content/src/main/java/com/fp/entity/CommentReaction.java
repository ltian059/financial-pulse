package com.fp.entity;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

/// CommentReaction entity class. Reaction here means comment likes.
/// 
@Entity
@Table(
        name = "comment_reactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"account_id", "comment_id"})
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(name = "created_at", nullable = false)
    @Default
    private Instant createdAt = Instant.now();

}
