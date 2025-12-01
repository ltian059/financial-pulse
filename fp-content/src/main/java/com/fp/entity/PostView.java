package com.fp.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

///
/// PostView entity class for post_views table.
/// 

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(
        name = "post_views",
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_post_views_account", columnNames = {"post_id", "account_id"}),
            @UniqueConstraint(name = "uq_post_views_client", columnNames = {"post_id", "client_hash"})
        }
)
public class PostView {
    
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "account_id")
    private String accountId;

    // Optional client hash to identify unique views from the same client
    // Useful for tracking unique views without requiring user accounts
    @Column(name = "client_hash")
    private String clientHash;
    
    @Column(name = "created_at", nullable = false)
    @Default
    private Instant createdAt = Instant.now();
}
