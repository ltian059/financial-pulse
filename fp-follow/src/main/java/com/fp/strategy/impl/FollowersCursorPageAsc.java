package com.fp.strategy.impl;

import com.fp.entity.Follow;
import com.fp.pattern.annotation.StrategyComponent;
import com.fp.repository.FollowRepository;
import com.fp.strategy.AbstractFollowQueryStrategy;
import com.fp.strategy.FollowQueryRequest;
import com.fp.strategy.FollowQueryStrategyType;

import java.util.List;

@StrategyComponent
public class FollowersCursorPageAsc extends AbstractFollowQueryStrategy {
    public FollowersCursorPageAsc(FollowRepository followRepository) {
        super(followRepository);
    }

    @Override
    public List<Follow> executeQuery(FollowQueryRequest input) {
        return followRepository.findAllFollowersWithCursorAsc(
                input.getAccountId(),
                input.getCursorTimestamp(),
                input.getCursorId(),
                input.getPageable()
        );
    }

    @Override
    protected FollowQueryStrategyType getStrategyType() {
        return FollowQueryStrategyType.FOLLOWERS_CURSOR_PAGE_ASC;
    }
}
