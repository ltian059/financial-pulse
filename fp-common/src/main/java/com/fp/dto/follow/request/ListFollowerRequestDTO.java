package com.fp.dto.follow.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ListFollowerRequestDTO {
    /**
     * The number of followerIds to return for each page.
     */
    @Min(1) @Max(100)
    private Integer limit = 20;


    ///
    /// # Cursor for pagination, indicating the position to start fetching followers.
    ///
    /// cursor format: createdAt timestamp:followerId
    ///
    /// Example: 1700000000:uuid-1234-5678-90ab-cdef12345678
    ///
    /// createdAt_timestamp: Unix timestamp in milliseconds
    ///
    /// followerId: The ID of the follower
    ///
    private String cursor;

    @Pattern(regexp = "asc|desc", message = "Order must be either 'asc' or 'desc'")
    private String order = "desc"; // Default order is descending

}
