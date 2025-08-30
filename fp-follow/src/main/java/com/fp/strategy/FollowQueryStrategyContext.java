package com.fp.strategy;

import com.fp.dto.follow.response.FollowProjection;
import com.fp.pattern.core.strategy.StrategyContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FollowQueryStrategyContext extends StrategyContext<FollowQueryRequest, List<FollowProjection>> {

    /**
     * Executes the follow query strategy based on the provided input.
     * @param input the request containing the query parameters
     * @return a list of Follow entities matching the query
     */
    public List<FollowProjection> executeFollowQuery(FollowQueryRequest input) {
        return super.executeStrategy(input);
    }

}
