package com.fp.follow.service;

public interface FollowService {

    Long getFollowerCount(Long accountId);

    void followAccount(Long followerId, Long followeeId);
}
