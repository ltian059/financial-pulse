package com.fp.service;

public interface FollowService {

    Long getFollowerCount(String accountId);

    void followAccount(String followerId, String followeeId);
}
