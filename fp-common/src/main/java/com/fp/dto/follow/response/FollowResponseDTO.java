package com.fp.dto.follow.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowResponseDTO implements FollowProjection {
    private String accountId;
    private Instant createdAt;
}
