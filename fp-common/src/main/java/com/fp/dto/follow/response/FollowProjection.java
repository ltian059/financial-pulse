package com.fp.dto.follow.response;

import java.time.Instant;

/**
 * Common projection interface for follow-related queries
 * This allows unified handling of both follower and followee data
 */
public interface FollowProjection {
    String getAccountId();
    Instant getCreatedAt();
}