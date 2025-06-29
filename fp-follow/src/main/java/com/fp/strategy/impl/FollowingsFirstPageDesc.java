package com.fp.strategy.impl;

import com.fp.entity.Follow;
import com.fp.pattern.annotation.StrategyComponent;
import com.fp.repository.FollowRepository;
import com.fp.strategy.AbstractFollowQueryStrategy;
import com.fp.strategy.FollowQueryRequest;
import com.fp.strategy.FollowQueryStrategyType;

import java.util.List;

@StrategyComponent
public class FollowingsFirstPageDesc extends AbstractFollowQueryStrategy {
    public FollowingsFirstPageDesc(FollowRepository followRepository) {
        super(followRepository);
    }

    @Override
    public List<Follow> executeQuery(FollowQueryRequest input) {
        return followRepository.findFirstPageFolloweesDesc(
                input.getAccountId(),
                input.getPageable()
        );
    }

    @Override
    protected FollowQueryStrategyType getStrategyType() {
        return FollowQueryStrategyType.FOLLOWINGS_FIRST_PAGE_DESC;
    }
}
