package com.fp.follow.service.impl;

import com.fp.exception.DuplicatedFollowException;
import com.fp.exception.SelfFollowNotAllowedException;
import com.fp.follow.entity.Follow;
import com.fp.follow.repository.FollowRepository;
import com.fp.follow.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @Override
    public void followAccount(Long followerId, Long followeeId) {
        if(followerId.equals(followeeId)){
            throw new SelfFollowNotAllowedException();
        }
        // Check if the follow relationship already exists
        Optional<Follow> optional = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        if(!optional.isPresent()){
            //create a new follow relationship
            Follow follow = Follow.builder()
                    .followerId(followerId)
                    .followeeId(followeeId)
                    .createdAt(LocalDateTime.now())
                    .build();
            followRepository.save(follow);
        }else{
            throw new DuplicatedFollowException("You have already followed this account.");
        }
    }
}
