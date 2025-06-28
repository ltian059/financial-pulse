package com.fp.service.impl;

import com.fp.dto.follow.request.FollowRequestDTO;
import com.fp.dto.follow.request.UnfollowRequestDTO;
import com.fp.exception.business.DuplicatedFollowException;
import com.fp.exception.business.FollowRelationshipNotFoundException;
import com.fp.exception.business.SelfFollowNotAllowedException;
import com.fp.entity.Follow;
import com.fp.repository.FollowRepository;
import com.fp.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
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
    public void follow(FollowRequestDTO followRequestDTO) {
        var followeeId = followRequestDTO.getFolloweeId();
        var followerId = followRequestDTO.getAccountId();
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

    @Override
    public void unfollow(UnfollowRequestDTO unfollowRequestDTO) {
        var followeeId = unfollowRequestDTO.getFolloweeId();
        var followerId = unfollowRequestDTO.getAccountId();
        // Check if the follow relationship exists
        Optional<Follow> optional = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        if(optional.isPresent()){
            //delete the follow relationship
            followRepository.delete(optional.get());
        }else{
            throw new FollowRelationshipNotFoundException();
        }
    }

    @Override
    public List<String> listFollower(String accountId) {
        // Get the list of followers for the account
        return followRepository.findFollowerIdsByFolloweeId(accountId);
    }

    @Override
    public List<String> listFollowing(String accountId) {
        return followRepository.findFolloweeIdsByFollowerId(accountId);
    }
}
