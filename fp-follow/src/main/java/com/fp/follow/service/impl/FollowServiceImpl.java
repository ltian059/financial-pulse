package com.fp.follow.service.impl;

import com.fp.follow.repository.FollowRepository;
import com.fp.follow.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private FollowRepository followRepository;

    /**
     * Get the number of followers for a specific account.
     * The current accountId is the followeeId.
     * @param accountId
     * @return the number of followers for the account
     */
    @Override
    public Long getFollowerCount(Long accountId) {
        //Get the number of people following this account
        //followeeId = accountId
        return followRepository.countFollowByFolloweeId(accountId);
    }
}
