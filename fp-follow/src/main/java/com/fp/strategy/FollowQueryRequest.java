package com.fp.strategy;

import com.fp.dto.follow.request.FollowPaginationRequestDTO.QueryType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

@Data
@Builder
public class FollowQueryRequest {
    private final String accountId;
    private final QueryType queryType;
    private final boolean isFirstPage;
    private final boolean isDescending;
    private final Instant cursorTimestamp;
    private final String cursorId;
    private final Pageable pageable;

    public FollowQueryStrategyType getStrategyType() {
        return FollowQueryStrategyType.from(queryType, isFirstPage, isDescending);
    }
}
