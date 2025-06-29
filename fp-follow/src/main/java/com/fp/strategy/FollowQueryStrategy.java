package com.fp.strategy;

import com.fp.entity.Follow;
import com.fp.pattern.core.strategy.Strategy;

import java.util.List;

public interface FollowQueryStrategy extends Strategy<FollowQueryRequest, List<Follow>> {

    /**
     * Executes the query based on the provided input.
     * @param input the request containing the query parameters
     * @return a list of Follow entities matching the query
     */
    List<Follow> executeQuery(FollowQueryRequest input);

    @Override
    default List<Follow> execute(FollowQueryRequest input) {
        return executeQuery(input);
    }

}
