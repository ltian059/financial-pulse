package com.fp.follow.service.impl;

import com.fp.common.exception.business.DuplicatedFollowException;
import com.fp.common.exception.business.SelfFollowNotAllowedException;
import com.fp.follow.entity.Follow;
import com.fp.follow.repository.FollowRepository;
import com.fp.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;


    /**
     * Get the number of followers for a specific account.
     * The current accountId is the followee ID.
     * @param accountId
     * @return the number of followers for the account
     */
    @Override
    public Long getFollowerCount(String accountId) {
        //Get the number of people following this account
        //followeeId = accountId
        return followRepository.countFollowByFolloweeId(accountId);
    }

    @Override
    public void followAccount(String followerId, String followeeId) {
        if(followerId.equals(followeeId)){
            throw new SelfFollowNotAllowedException();
        }
        // Check if the follow relationship already exists
        Optional<Follow> optional = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        if(optional.isEmpty()){
            //create a new follow relationship
            Follow follow = Follow.builder()
                    .followerId(followerId)
                    .followeeId(followeeId)
                    .createdAt(Instant.now())
                    .build();
            followRepository.save(follow);
        }else{
            throw new DuplicatedFollowException();
        }
    }
}
