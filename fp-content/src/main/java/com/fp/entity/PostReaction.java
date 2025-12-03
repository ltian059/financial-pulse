package com.fp.entity;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "post_reactions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_post_reactions_post_account_type", columnNames = {"post_id", "account_id", "type"}),
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "created_at", nullable = false)
    @Default
    private Instant createdAt = Instant.now();

    public enum Type {
        LIKE,
        REPOST,
        QUOTE
    }

}
