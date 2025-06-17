package com.fp.follow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "follows")
@IdClass(FollowId.class) // Use composite key for followerId and followeeId
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follow {
    @Id
    private Long followerId;

    @Id
    private Long followeeId;

    @Column(nullable = false)
    private Instant createdAt;
}

