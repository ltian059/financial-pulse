package com.fp.dto.content;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
///
/// Request Params for listing posts.
///
@Data
@Builder
public class ListPostsRequestDTO {
    // The ID of the account for which to list posts.
    private String accountId;

    // The number of posts to return for each page.
    @Builder.Default
    private Integer limit = 20;

    /// - Cursor for pagination, indicating the position to start fetching posts.
    ///
    /// - Post sorting rule: if modifiedAt is not null, sort by modifiedAt desc; else sort by createdAt desc. Always use **descending** order.
    ///
    /// - cursor format: `effectiveAt#postId`
    ///
    /// - `effectiveAt` is either `modifiedAt` or `createdAt` based on the sorting rule.
    /// - Example: `2025-06-29-11:50:3233545Z#19`
    private String cursor;

    // The keyword to filter posts by content.
    private String keyword;

    // The status to filter posts (ACTIVE, DRAFT, HIDDEN).
    @Pattern(regexp = "ACTIVE|DRAFT|HIDDEN", message = "Status must be 'ACTIVE', 'DRAFT', or 'HIDDEN'")
    private String status;


}
