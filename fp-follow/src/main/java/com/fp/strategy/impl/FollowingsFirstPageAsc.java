package com.fp.strategy.impl;

import com.fp.dto.follow.response.FollowProjection;
import com.fp.pattern.annotation.StrategyComponent;
import com.fp.repository.FollowRepository;
import com.fp.strategy.AbstractFollowQueryStrategy;
import com.fp.strategy.FollowQueryRequest;
import com.fp.strategy.FollowQueryStrategyType;

import java.util.List;

@StrategyComponent
public class FollowingsFirstPageAsc extends AbstractFollowQueryStrategy {
    public FollowingsFirstPageAsc(FollowRepository followRepository) {
        super(followRepository);
    }

    @Override
    public List<FollowProjection> executeQuery(FollowQueryRequest input) {
        return followRepository.findFirstPageFolloweesAsc(
                input.getAccountId(),
                input.getPageable()
        );
    }


    @Override
    protected FollowQueryStrategyType getStrategyType() {
        return FollowQueryStrategyType.FOLLOWINGS_FIRST_PAGE_ASC;
    }
}
