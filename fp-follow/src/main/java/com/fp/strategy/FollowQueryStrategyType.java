package com.fp.strategy;

import com.fp.dto.follow.request.FollowPaginationRequestDTO.QueryType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FollowQueryStrategyType {
    // Followers Query Strategies
    FOLLOWERS_FIRST_PAGE_ASC(QueryType.FOLLOWERS, true, false),
    FOLLOWERS_FIRST_PAGE_DESC(QueryType.FOLLOWERS, true, true),
    FOLLOWERS_CURSOR_PAGE_ASC(QueryType.FOLLOWERS, false, false),
    FOLLOWERS_CURSOR_PAGE_DESC(QueryType.FOLLOWERS, false, true),

    // Following Query Strategies
    FOLLOWINGS_FIRST_PAGE_ASC(QueryType.FOLLOWINGS, true, false),
    FOLLOWINGS_FIRST_PAGE_DESC(QueryType.FOLLOWINGS, true, true),
    FOLLOWINGS_CURSOR_PAGE_ASC(QueryType.FOLLOWINGS, false, false),
    FOLLOWINGS_CURSOR_PAGE_DESC(QueryType.FOLLOWINGS, false, true)

    ;

    private final QueryType queryType;
    private final boolean isFirstPage;
    private final boolean isDescending;


    /**
     * Return the specific FollowQueryRequestType based on the queryType, isFirstPage, and isDescending.
     */
    public static FollowQueryStrategyType from(QueryType queryType, boolean isFirstPage, boolean isDescending) {
        for(var type: FollowQueryStrategyType.values()){
            if(type.queryType == queryType && type.isFirstPage == isFirstPage && type.isDescending == isDescending) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                String.format("No strategy found for queryType=%s, isFirstPage=%s, isDescending=%s",
                queryType, isFirstPage, isDescending)
        );
    }

}
