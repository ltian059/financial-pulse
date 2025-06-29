package com.fp.strategy;

import com.fp.entity.Follow;
import com.fp.pattern.core.strategy.StrategyContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FollowQueryStrategyContext extends StrategyContext<FollowQueryRequest, List<Follow>> {

    /**
     * Executes the follow query strategy based on the provided input.
     * @param input the request containing the query parameters
     * @return a list of Follow entities matching the query
     */
    public List<Follow> executeFollowQuery(FollowQueryRequest input) {
        return super.executeStrategy(input);
    }

}
