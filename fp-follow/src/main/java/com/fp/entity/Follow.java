package com.fp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "follows")
@IdClass(FollowId.class) // Use composite key for followerId and followeeId
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Follow {
    @Id
    private String followerId;

    @Id
    private String followeeId;

    @Column(nullable = false)
    private Instant createdAt;
}

