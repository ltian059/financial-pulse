package com.fp.strategy.impl;

import com.fp.dto.follow.response.FollowProjection;
import com.fp.pattern.annotation.StrategyComponent;
import com.fp.repository.FollowRepository;
import com.fp.strategy.AbstractFollowQueryStrategy;
import com.fp.strategy.FollowQueryRequest;
import com.fp.strategy.FollowQueryStrategyType;

import java.util.List;
@StrategyComponent
public class FollowersFirstPageDesc extends AbstractFollowQueryStrategy {
    public FollowersFirstPageDesc(FollowRepository followRepository) {
        super(followRepository);
    }

    @Override
    public List<FollowProjection> executeQuery(FollowQueryRequest input) {
        return followRepository.findFirstPageFollowersDesc(
                input.getAccountId(),
                input.getPageable()
        );
    }

    @Override
    protected FollowQueryStrategyType getStrategyType() {
        return FollowQueryStrategyType.FOLLOWERS_FIRST_PAGE_DESC;
    }
}
