package com.fp.dto.follow.request;

import com.fp.constant.PageConstant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListFollowersRequestDTO {

    /// The ID of the following account for which to list followers.
    private String accountId; // == FolloweeId


    /**
     * The number of followerIds to return for each page.
     */
    @Min(1) @Max(100)
    private Integer limit = PageConstant.DEFAULT_PAGE_SIZE;

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

    /// The order in which to return the followers.
    @Pattern(regexp = "asc|desc", message = "Order must be either 'asc' or 'desc'")
    private String order = PageConstant.DESC; // Default order is descending

}
