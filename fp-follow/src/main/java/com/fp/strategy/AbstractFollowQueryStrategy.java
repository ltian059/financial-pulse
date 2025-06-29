package com.fp.strategy;

import com.fp.repository.FollowRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractFollowQueryStrategy implements FollowQueryStrategy {
    protected final FollowRepository followRepository;
    /**
     * Every Strategy must declare its own query strategy type.
     * This is used to determine if the strategy supports a given request.
     */
    protected abstract FollowQueryStrategyType getStrategyType();

    /**
     * Checks if the strategy supports the given request.
     * If the strategy type matches the request's strategy type, it returns true.
     */
    @Override
    public boolean supports(FollowQueryRequest request) {
        return getStrategyType().equals(request.getStrategyType());
    }

    @Override
    public String getStrategyName() {
        return getStrategyType().name();
    }
}
