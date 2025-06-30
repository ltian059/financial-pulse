package com.fp.strategy.impl;

import com.fp.entity.Follow;
import com.fp.pattern.annotation.StrategyComponent;
import com.fp.repository.FollowRepository;
import com.fp.strategy.AbstractFollowQueryStrategy;
import com.fp.strategy.FollowQueryRequest;
import com.fp.strategy.FollowQueryStrategyType;

import java.util.List;

@StrategyComponent
public class FollowingsCursorPageAsc extends AbstractFollowQueryStrategy {
    public FollowingsCursorPageAsc(FollowRepository followRepository) {
        super(followRepository);
    }

    @Override
    public List<Follow> executeQuery(FollowQueryRequest input) {
        return followRepository.findAllFolloweesWithCursorAsc(
                input.getAccountId(),
                input.getCursorTimestamp(),
                input.getCursorId(),
                input.getPageable()
        );
    }

    @Override
    protected FollowQueryStrategyType getStrategyType() {
        return FollowQueryStrategyType.FOLLOWINGS_CURSOR_PAGE_ASC;
    }
}
