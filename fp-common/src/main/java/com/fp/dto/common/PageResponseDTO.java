package com.fp.dto.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponseDTO <T>{
    List<T> data;

    ///
    /// # Cursor for pagination, indicating the position to start fetching followers.
    ///
    /// Follow table cursor format: createdAt timestamp:followerId
    ///
    /// Example: 1700000000:uuid-1234-5678-90ab-cdef12345678
    ///
    /// createdAt_timestamp: Unix timestamp in milliseconds
    ///
    /// followerId: The ID of the follower
    ///
    private String nextCursor;
    boolean hasMore;
    private Integer limit;
}
