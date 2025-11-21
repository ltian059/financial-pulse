package com.fp.strategy;

import com.fp.dto.follow.response.FollowProjection;
import com.fp.pattern.core.strategy.Strategy;

import java.util.List;

public interface FollowQueryStrategy extends Strategy<FollowQueryRequest, List<FollowProjection>> {

    /**
     * Executes the query based on the provided input.
     * @param input the request containing the query parameters
     * @return a list of projections matching the query
     */
    List<FollowProjection> executeQuery(FollowQueryRequest input);

    @Override
    default List<FollowProjection> execute(FollowQueryRequest input) {
        return executeQuery(input);
    }

}
